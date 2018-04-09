package com.ephox.flax
package api.specs2

import com.ephox.flax.api.action.Log.single
import com.ephox.flax.api.action.{Action, Err, Log}
import com.ephox.flax.api.action.FlaxActions._
import com.ephox.flax.api.elem.Elem
import org.openqa.selenium.By
import org.specs2.execute.Result

import scalaz.{Writer, \/}
import scalaz.effect.IO
import org.specs2.matcher.MatchResult
import org.specs2.matcher.MustMatchers._

object FlaxAssertions {

  def assert[A](matchResult: => MatchResult[A]): Action[Unit] =
    Action.fromDiowe { _ =>
      IO {
        val result: Result = matchResult.toResult

        val resultS: Log[String] = single[String](result.toString)

        val e = if (result.isFailure)
          \/.left[Err, Unit](Err.assertionFailed(result.message))
        else
          \/.right[Err, Unit](())

        Writer(resultS, e)
      }
    }

  def assertTrue(b: => Boolean): Action[Unit] =
    assert(b must beTrue)

  def assertEquals[T](a: => T, b: => T): Action[Unit] =
    assert(a must_=== b)

  def assertAction(action: Action[Boolean]): Action[Unit] = for {
    b <- action
    _ <- assertTrue(b)
  } yield ()


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

