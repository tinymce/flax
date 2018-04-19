package com.ephox.flax
package api.specs2

import com.ephox.flax.api.action.FlaxActions._
import com.ephox.flax.api.action.Log.single
import com.ephox.flax.api.action.{Action, Err, Log}
import com.ephox.flax.api.elem.Elem
import org.openqa.selenium.By
import org.specs2.execute
import org.specs2.execute._
import org.specs2.matcher.MatchResult
import org.specs2.matcher.MustMatchers._

import scala.annotation.tailrec
import scalaz._
import scalaz.effect.IO
import scalaz.syntax.monad._

object FlaxAssertions {

  // TODO: test this
  @tailrec
  private[flax] def resultToEither(r: Result): Result \/ Unit = r match {
    case DecoratedResult(_, nested) => resultToEither(nested)
    case execute.Success(_, _) | Skipped(_, _) => \/.right(())
    case execute.Failure(_, _, _, _) | Error(_, _) | Pending(_) => \/.left(r)
  }

  def assert[A](matchResult: => MatchResult[A]): Action[Unit] = {
    Action.fromSideEffect_(matchResult.toResult).flatMap { result =>
      Action.fromDiowe_(IO {
        val resultS: Log[String] = single[String](result.toString)
        val z = resultToEither(result).leftMap(x => Err.assertionFailed(x.message))
        Writer(resultS, z)
      })
    }
  }

  def assertTrue(b: Boolean): Action[Unit] =
    assert(b must beTrue)

  def assertEquals[T](a: => T, b: => T): Action[Unit] =
    assert(a must_=== b)

  def assertAction(action: Action[Boolean]): Action[Unit] =
    action >>= assertTrue

  def assertSelected(e: Elem): Action[Unit] =
    assertAction(isSelected(e))

  def assertEnabled(e: Elem): Action[Unit] =
    assertAction(isEnabled(e))

  def assertDisplayed(e: Elem): Action[Unit] =
    assertAction(isDisplayed(e))


  def assertNotSelected(e: Elem): Action[Unit] =
    assertAction(isSelected(e).not)

  def assertNotEnabled(e: Elem): Action[Unit] =
    assertAction(isEnabled(e).not)

  def assertNotDisplayed(e: Elem): Action[Unit] =
    assertAction(isDisplayed(e).not)


  def assertSelectedBy(by: By): Action[Unit] =
    assertAction(isSelectedBy(by))

  def assertEnabledBy(by: By): Action[Unit] =
    assertAction(isEnabledBy(by))

  def assertDisplayedBy(by: By): Action[Unit] =
    assertAction(isDisplayedBy(by))


  def assertNotSelectedBy(by: By): Action[Unit] =
    assertAction(isSelectedBy(by).not)

  def assertNotEnabledBy(by: By): Action[Unit] =
    assertAction(isEnabledBy(by).not)

  def assertNotDisplayedBy(by: By): Action[Unit] =
    assertAction(isDisplayedBy(by).not)


  def assertNotExistsNow(by: By): Action[Unit] =
    assertAction(exists(by).not)

  def assertExistsNow(by: By): Action[Unit] =
    assertAction(exists(by))
}

