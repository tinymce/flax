package com.ephox.flax
package api.action

import Predef.=:=
import Action._
import api.action.Log._
import api.elem.Driver
import PPrint._
import com.ephox.flax.internal.{ActionBase, RT, WT}
import org.openqa.selenium.support.ui.UnexpectedTagNameException

import scala.util.control.NonFatal
import scalaz._
import scalaz.syntax.applicative._
import scalaz.syntax.std.option.ToOptionOpsFromOption
import scalaz.EitherT.eitherT
import scalaz.effect.IO


/**
  * A computation designed to model an action, or set of actions performed by Selenium,
  * in a purely functional manner.
  *
  * The stack of data types captures the following features:
  *
  *   - Input of a [[Driver]] value to run Selenium commands.
  *   - Effect-tracking.
  *   - Accumulation of a tree of log messages, for diagnosing test failures.
  *   - Catching exceptions and other failure conditions.
  *   - Producing a value within this context.
  *
  * [[Action]] is a [[Monad]].
  *
  * @param run the underlying computation
  * @tparam A type of the value resulting from this computation
  */

final case class Action[A](run: ActionBase[A]) {

  def runOrThrow(d: Driver): A =
    run.run.run.run(d).unsafePerformIO() match {
      case (w, z) =>
        val log = "\nSteps performed:\n" + w.pprint + "\n"
        z match {
          case -\/(err) =>
            val (msg: String, ex: Option[Throwable]) = err match {
              case AssertionFailed(message) => ("Assertion failed: " + message, None)
              case CouldNotFindElement(by) => ("Could not find element: " + by.toString, None)
              case WrongElementType() => ("Wrong element type", None)
              case Kersploded(e) => ("Exception", Some(e))
              case Other(s) => (s, None)
            }
            throw new RuntimeException(msg + log, ex.orNull)
          case \/-(t) => t
        }
    }

  def toIO(d: Driver): IO[Writer[Log[String], Err \/ A]] =
    run.run.run.run(d).map { case (l, s) => Writer(l, s) }

  def mute: Action[A] =
    setLog(Log.empty)

  def setLog(log: Log[String]): Action[A] =
    mapLog(_ => log)

  def mapLog(fn: Log[String] => Log[String]): Action[A] =
    fromWriterT(run.run.mapWritten(fn))

  def map[B](fn: A => B): Action[B] =
    Functor[Action].map(this)(fn)

  def flatMap[B](faab: A => Action[B]): Action[B] =
    Bind[Action].bind(this)(faab)

  def not(implicit ev: A =:= Boolean): Action[Boolean] =
    map(!_)

  /** Run this action only if the argument is true.
    * Note that the payload value of this is discarded.
    */
  def onlyIf(b: Boolean): Action[Unit] =
    if (b) Functor[Action].void(this) else Action.noop

  /** Run this action only if the specified action succeeds with true.
    * Note that the payload value of this is discarded.
    */
  def onlyIfA(ab: Action[Boolean]): Action[Unit] =
    for {
      b <- ab
      _ <- this onlyIf b
    } yield ()

  def onlyIfSomeWithLog[B](noneMessage: String)(implicit ev: A =:= Option[B]): Action[B] =
    flatMap[B] { a =>
      ev(a) match {
        case Some(z) => Action.point[B](z)
        case None => Action.fromErr[B](Err.other(noneMessage))
      }
    }

  def onlyIfSome[B](implicit ev: A =:= Option[B]): Action[B] =
    onlyIfSomeWithLog("Expected Some, but was None")

  def repeat(n: Int): Action[Unit] =
    if (n <= 0) Action.noop
    else for {
      _ <- this
      _ <- repeat(n - 1)
    } yield ()

  def mapEither[B](f: Err \/ A => Err \/ B): Action[B] =
    Action(run.validationed(_.disjunctioned(f)))

  def attempt: Action[Err \/ A] =
    mapEither[Err \/ A](x => \/-(x.fold(\/.left, \/.right)))

  def unattempt[B](implicit ev: A =:= (Err \/ B)): Action[B] =
    flatMap(a => Action(EitherT.either(ev(a))))

  def onFinish[B](action: Action[B]): Action[A] =
    (attempt <* action).unattempt

  def unsafePerformIO(d: Driver): (Log[String], Err \/ A) =
    run.run.run.run(d).unsafePerformIO()
}

object Action {

  def fromErr[A](e: Err): Action[A] =
    fromDisjunction(-\/(e))

  def fromThrowable[A](t: Throwable): Action[A] =
    fromErr(Kersploded(t))

  /** Action that succeeds with a specified value */
  def point[A](a: => A): Action[A] =
    Applicative[Action].point(a)

  /** Action that succeeds and logs a message */
  def log(s: String): Action[Unit] =
    logList(DList(s))

  /** Action that succeeds and logs list of messages */
  def logList(s: DList[String]): Action[Unit] =
    fromIorwe(IO(Reader(_ => Writer(Log.fromDList(s), \/-(())))))

  /** Action that succeeds with the unit value */
  def noop: Action[Unit] =
    point(())

  /**
    * Wraps an impure function in an Action, catching any exceptions.
    */
  def fromSideEffect[A](run: Driver => A): Action[A] =
    fromSideEffectWithLogs(DList(), run)

  /**
    * Wraps an impure function in an Action, catching any exceptions.
    */
  def fromSideEffect_[A](run: => A): Action[A] =
    fromSideEffect(_ => run)

  /**
    * Wraps an impure function in an Action, catching any exceptions. Includes a log message.
    */
  def fromSideEffectWithLog[A](message: String, run: Driver => A): Action[A] =
    fromSideEffectWithLogs(DList(message), run)

  /**
    * Wraps an impure function in an Action, catching any exceptions. Includes a list of log messages.
    */
  def fromSideEffectWithLogs[A](messages: DList[String], f: Driver => A): Action[A] = {

    val m: Log[String] = Log.fromDList(messages)

    val value1: IO[Reader[Driver, Writer[Log[String], Err ∨ A]]] = IO(Reader { driver =>
      try {
        Writer(m, \/-(f(driver)))
      } catch {
        case e: UnexpectedTagNameException => Writer(m :+ "UnexpectedTagNameException thrown", -\/(Err.wrongElementType(e)))
        case NonFatal(t) => Writer(m :+ (t.getClass.getSimpleName + " thrown"), -\/(Err.kersploded(t)))
      }
    })
    fromIorwe(value1)
  }

  def fromIorwe[A](f: IO[Reader[Driver, Writer[Log[String], Err ∨ A]]]): Action[A] = {
    fromDiope(d => f.map(r => r(d).run))
  }

  def fromDiowe[A](fn: Driver => IO[Writer[Log[String], Err ∨ A]]): Action[A] =
    fromDiope(d => fn(d).map(_.run))

  def fromDiowe_[A](fn: => IO[Writer[Log[String], Err ∨ A]]): Action[A] =
    fromDiowe(_ => fn)

  def fromDiope[A](f: Driver => IO[(Log[String], Err ∨ A)]): Action[A] =
    Action(EitherT[WT, Err, A](WriterT[RT, Log[String], Err ∨ A](ReaderT.apply(f))))

  def fromDiope_[A](f: IO[(Log[String], Err ∨ A)]): Action[A] =
    fromDiope(_ => f)

  /** Nest an action or set of actions, providing a log message for the set. */
  def nested[A](message: String, action: Action[A]): Action[A] =
    action mapLog (logs => Log(DList(Node(message, logs))))

  /** An action that requires an Option[A] to be Some and produces the A from the Some. */
  def fromOption[A](b: => Option[A]): Action[A] =
    fromDisjunction(b toRightDisjunction AssertionFailed("Expected Some, but was None"))

  def fromDisjunction[A](d: => Err ∨ A): Action[A] =
    Action(EitherT.fromDisjunction(d))

  def fromWriterT[A](value: WriterT[RT, Log[String], Err ∨ A]) =
    Action(eitherT[WT, Err, A](value))

  implicit def ActionInstances: Monad[Action] =
    new Monad[Action] {
      override def point[A](a: => A): Action[A] =
        Action(Monad[ActionBase].point(a))

      override def map[A, B](fa: Action[A])(f: A => B): Action[B] =
        Action(Monad[ActionBase].map(fa.run)(f))

      override def bind[A, B](fa: Action[A])(faab: A => Action[B]): Action[B] =
        Action(Monad[ActionBase].bind(fa.run)(a => faab(a).run))
    }
}