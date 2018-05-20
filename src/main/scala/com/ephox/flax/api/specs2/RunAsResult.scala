package com.ephox.flax
package api.specs2

import api.elem.Driver
import api.action._
import PPrint._

import org.specs2.execute.{Error, Failure, Result, Success}

import scalaz.{-\/, \/-}

/** Runs an [[Action]] as a specs2 [[Result]].
  */
object RunAsResult {

  def runAsResult[T](a: Action[T], d: Driver): Result =
    a.run.run.run.run(d).unsafePerformIO() match {
      case (w, z) =>
        val log = "\nSteps performed:\n" + w.pprint + "\n!!! "
        z match {
          case -\/(AssertionFailed(message)) => Failure(log + "Assertion failed: " + message)
          case -\/(CouldNotFindElement(by)) => Failure(log + "Could not find element: " + by.toString)
          case -\/(WrongElementType()) => Failure(log + "Wrong element type")
          case -\/(Kersploded(e)) => Error(log + s"Test failed due to ${e.getClass.getSimpleName}\n", e)
          case -\/(Other(message)) => Failure(log + message)
          case \/-(t) => Success(log + t.toString)
        }
    }
}
