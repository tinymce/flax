package com.ephox.flax
package misc

import api.action.{Action, Err, Log}
import api.elem.Driver
import api.elem.Driver.driver

import scalaz.\/

object TestUtils {
  def unimplementedDriver: Driver =
    driver(UnimplementedWebDriver)


  def run[T](action: Action[T]): (Log[String], Err \/ T) = {
    action
      .toIO(unimplementedDriver)
      .unsafePerformIO().run
  }

  def runAndGetLogs[T](action: Action[T]): Log[String] =
    run(action)._1

  def runAndGetValue[T](action: Action[T]): Err \/ T =
    run(action)._2
}
