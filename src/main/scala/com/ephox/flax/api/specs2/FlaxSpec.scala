package com.ephox.flax
package api.specs2

import api.action.Action.noop
import api.action.{Action, FlaxActions}
import api.elem.Driver.driverForBrowser
import api.elem.{Browser, Driver}
import RunAsResult.runAsResult
import org.specs2.execute.Result
import org.specs2.specification.{BeforeAfterAll, BeforeAfterEach}
import scalaz.syntax.applicative._

/**
  * specs2 mixin for Flax tests.
  *
  * Usage:
  *
  * {{{
  *   trait MyFlax extends FlaxSpec {
  *     override def curBrowser: Browser = Firefox
  *
  *     //optional
  *     override def beforeAllAction: Action[Unit] = ...
  *
  *     //optional
  *     override def afterAllAction: Action[Unit] = ...
  *   }
  * }}}
  *
  * {{{
  * class MySpec extends Specification with MyFlax {
  *   sequential
  *   ...
  * }
  * }}}
  *
  * This is mainly an example - you may wish to integrate into specs2 differently.
  */
trait FlaxSpec extends BeforeAfterAll with BeforeAfterEach {

  def curBrowser: Browser

  def beforeAllAction: Action[Unit] = noop

  def beforeEachAction: Action[Unit] = noop

  def afterEachAction: Action[Unit] = noop

  def afterAllAction: Action[Unit] = noop

  override final def beforeAll(): Unit =
    FlaxSpec.runBeforeAllAction(curBrowser, beforeAllAction)


  override final def before(): Unit = {
    FlaxSpec.runBeforeEachAction(beforeEachAction)
  }

  override final def after(): Unit = {
    FlaxSpec.runAfterEachAction(afterAllAction)
  }

  override final def afterAll(): Unit =
    FlaxSpec.runAfterAllAction(afterAllAction)

  implicit final def runTest[A](action: Action[A]): Result = {
    FlaxSpec.runTestAction(action)
  }
}

private[flax] object FlaxSpec {
  // Argh! Mutation! I blame Specs2.

  private var driver: Option[Driver] = None

  def runBeforeAllAction[T](b: Browser, a: Action[T]): Unit =
    synchronized {
      if (driver.isEmpty) {
        val d = driverForBrowser(b)
        driver = Some(d)
      }
      driver.foreach(a.runOrThrow)
      ()
    }

  def runBeforeEachAction(a: Action[Unit]): Unit =
    synchronized {
      driver.fold(println("Driver not loaded - skipping beforeEachAction"))(a.runOrThrow)
    }

  def runTestAction[A](a: Action[A]): Result =
    synchronized {
      driver.fold(throw new RuntimeException("Driver not loaded - cannot run test"))(d => runAsResult(a, d))
    }

  def runAfterEachAction(a: Action[Unit]): Unit =
    synchronized {
      driver.fold(println("Driver not loaded - skipping afterEachAction"))(a.runOrThrow)
    }

  def runAfterAllAction[T](a: Action[T]): Unit = {
    synchronized {
      driver.fold(println("Driver not loaded - skipping afterAllAction"))(a.void.onFinish(FlaxActions.quit).runOrThrow)
      driver = None
    }
    ()
  }
}