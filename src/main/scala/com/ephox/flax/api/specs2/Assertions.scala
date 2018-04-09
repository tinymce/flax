package com.ephox.flax
package api.specs2

import com.ephox.flax.api.action.Log.single
import com.ephox.flax.api.action.{Action, Err}
import org.specs2.matcher.MatchResult
import org.specs2.matcher.MustMatchers._

import scalaz.effect.IO
import scalaz.{-\/, Writer, \/-}

/** Specs2 assertions lifted into [[Action]] values */
trait Assertions {

  def assert[A](matchResult: => MatchResult[A]): Action[Unit] =
    Action.fromDiowe { d => IO {
      val result = matchResult.toResult
      val resultS = result.toString
      if (result.isFailure) Writer(single[String](resultS), -\/(Err.assertionFailed(result.message)))
      else Writer(single(resultS), \/-(()))
    }}

  def assertTrue(b: => Boolean): Action[Unit] =
    assert(b must beTrue)

  def assertEquals[T](a: => T, b: => T): Action[Unit] =
    assert(a must_=== b)
}

object Assertions extends Assertions
