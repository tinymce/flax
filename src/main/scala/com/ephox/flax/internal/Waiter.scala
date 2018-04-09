package com.ephox.flax
package internal

import com.ephox.flax.api.action.{Action, Err, Log}

import scala.annotation.tailrec
import scalaz._
import scalaz.syntax.applicative._
import scalaz.effect.IO, IO._

object Waiter {

  /**
    * Poll an operation until it returns [[Some]]
    */
  def waitFor[T](run: IO[Option[T]], timeoutMillis: Long = 10000, pollDelay: Long = 500): IO[Option[T]] =
  IO {
    val start = System.currentTimeMillis
    def timedOut = System.currentTimeMillis - start > timeoutMillis

    @tailrec
    def poll: Option[T] =
      run.unsafePerformIO() match {
        case t@Some(_) =>
          t
        case None =>
          if (timedOut) {
            None
          } else {
            Thread.sleep(pollDelay)
            poll
          }
      }
    poll
  }

  /**
    * Poll an Action, while it succeeds with false, until it succeeds with true.
    * If it fails or throws, terminate.
    * Result from final poll is returned.
    */
  def waitForActionToReturnTrue(action: Action[Boolean], timeoutMillis: Long = 10000, pollDelay: Long = 500): Action[Unit] =
    Action.fromDiowe { d =>
      // TODO: Remove the unsafePerformIO calls
      IO {
        val start = System.currentTimeMillis
        def timedOut = System.currentTimeMillis - start > timeoutMillis

        def poll: IO[Writer[Log[String], Err \/ Unit]] =
          action.run.run.run(d).unsafePerformIO() match {
            case (w, \/-(true)) =>
              IO(Writer(w, \/-(())))
            case (w, \/-(false)) =>
              if (timedOut) {
                IO(Writer(w, -\/(Err.other("Timed out waiting for condition"))))
              } else {
                IO(Thread.sleep(pollDelay)) *> poll
              }
            case (w, -\/(err)) =>
              IO(Writer(w, -\/(err)))
          }
        poll.unsafePerformIO()
      }
    }

  /**
    * Poll an Action, while it succeeds with false, until it succeeds with true.
    * If it fails or throws, terminate.
    * Result from final poll is returned.
    */
  def waitForActionToSucceed[T](action: Action[T], timeoutMillis: Long = 10000, pollDelay: Long = 500): Action[T] =
    Action.fromDiowe { d =>
      IO {
        val start = System.currentTimeMillis
        def timedOut = System.currentTimeMillis - start > timeoutMillis

        @tailrec
        def poll: Writer[Log[String], Err \/ T] =
          action.run.run.run(d).unsafePerformIO() match {
            case r@(w, \/-(t)) =>
              Writer(w, \/-(t))
            case (w, -\/(_)) =>
              if (timedOut) {
                Writer(w, -\/(Err.other("Timed out waiting for condition")))
              } else {
                Thread.sleep(pollDelay)
                poll
              }
          }
        poll
      }
    }
}
