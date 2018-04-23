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
        val log = "\nSteps performed:\n" + w.pprint + "\n"
        z match {
          case -\/(AssertionFailed(message)) => Failure("Assertion failed: " + message + log)
          case -\/(CouldNotFindElement(by)) => Failure("Could not find element: " + by.toString + log)
          case -\/(WrongElementType()) => Failure("Wrong element type" + log)
          case -\/(Kersploded(e)) => Error(s"Test failed due to ${e.getClass.getSimpleName}\n" + log, e)
          case -\/(Other(message)) => Failure(message + log)
          case \/-(t) => Success(t.toString + log)
        }
    }
}
