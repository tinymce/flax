package com.ephox.flax
package api.action

import Action.fromSideEffectWithLog
import Err.couldNotFindElement
import Log.single
import api.elem.Elem.elem
import api.elem.Driver
import api.elem.Elem
import api.elem.Selekt
import Selekt.selekt
import com.ephox.flax.api.wait.WaitOptions
import internal.Waiter
import org.openqa.selenium.{By, Cookie, JavascriptExecutor, WebDriver}
import org.openqa.selenium.WebDriver.{Navigation, Options}
import org.openqa.selenium.support.ui.Select

import scala.collection.JavaConverters._
import scalaz.effect.IO
import scalaz._

object FlaxActions {

  private def findElement(d: Driver, by: By): IO[Option[Elem]] =
    findElements(d, by).map(_.headOption)

  private def findElements(d: Driver, by: By): IO[List[Elem]] =
    IO(d.d.findElements(by).asScala.toList.map(elem(_, by)))

  def get(url: String): Action[Unit] =
    fromSideEffectWithLog("Opening URL: " + url, _.d.get(url))

  /** Finds the "first" element defined by the By selector, polling until a default timeout. */
  def find(by: By): Action[Elem] =
    findWithWaitOptions(by, WaitOptions.default)

  /** Finds the "first" element defined by the By selector, polling until a default timeout. */
  def findWithWaitOptions(by: By, wo: WaitOptions): Action[Elem] =
    Action.fromDiowe { d =>
      Waiter.waitFor(findElement(d, by), wo) map (z => Writer(single("Finding element: " + by), ErrOrElem.fromOption(couldNotFindElement(by))(z)))
    }

  /** Finds the "first" element defined by the By selector. */
  def findNow(by: By): Action[Elem] =
    Action.fromDiowe { d =>
      findElement(d, by) map (z => Writer(single("Finding element (immediately): " + by), ErrOrElem.fromOption(couldNotFindElement(by))(z)))
    }

  def findAllNow(by: By): Action[List[Elem]] =
    Action.fromDiowe { d =>
      findElements(d, by) map { z =>
        Writer(single("Finding elements (immediately): " + by), \/.right(z))
      }
    }

  /** Does an element matching the By selector exist in the DOM right now? */
  def exists(by: By): Action[Boolean] =
    Action.fromDiowe { d =>
      findElement(d, by) map { e =>
        val b = e.isDefined
        val msg = (if (b) "Element exists: " else "Element does not exist: ") + by.toString
        Writer(single(msg), \/-(b))
      }
    }

  /** Clicks the specified element  */
  def click(e: Elem): Action[Unit] =
    fromSideEffectWithLog("Clicking element: " + e.by, _ => e.e.click())

  /** Finds the "first" element defined by the By selector (polling until a default timeout), then clicks it. */
  def clickBy(by: By): Action[Unit] =
    findAnd(click, by)

  def findAnd[T](f: Elem => Action[T], by: By): Action[T] =
    findWithWaitOptionsAnd(f, by, WaitOptions.default)

  def findWithWaitOptionsAnd[T](f: Elem => Action[T], by: By, wo: WaitOptions): Action[T] =
    for {
      e <- findWithWaitOptions(by, wo)
      s <- f(e)
    } yield s

  def findNowAnd[T](f: Elem => Action[T], by: By): Action[T] =
    for {
      e <- findNow(by)
      s <- f(e)
    } yield s


  def typeIn(e: Elem, text: String): Action[Unit] =
    fromSideEffectWithLog(s"""Typing "$text" in element: ${e.by}""", _ => e.e.sendKeys(text))

  def clear(e: Elem): Action[Unit] =
    fromSideEffectWithLog(s"""Clearing element: ${e.by}""", _ => e.e.clear())

  def clearBy(by: By): Action[Unit] =
    findAnd(clear, by)

  def typeInBy(by: By, text: String): Action[Unit] =
    findAnd(typeIn(_, text), by)

  def setText(e: Elem, text: String): Action[Unit] =
    for {
      _ <- clear(e)
      _ <- typeIn(e, text)
    } yield ()

  def setTextBy(by: By, text: String): Action[Unit] =
    findAnd(setText(_, text), by)

  def switchToFrame(e: Elem): Action[Unit] =
    fromSideEffectWithLog("Switching to frame:" + e.by, { d =>
      d.d.switchTo().frame(e.e)
      ()
    })

  def switchToFrameBy(by: By): Action[Unit] =
    findAnd(switchToFrame, by)

  def asSelekt(e: Elem): Action[Selekt] =
    fromSideEffectWithLog("Selecting element: " + e.by, _ => selekt(new Select(e.e), e.by))

  def findSelekt(by: By): Action[Selekt] =
    findAnd(asSelekt, by)

  def selectByValue(s: Selekt, value: String): Action[Unit] =
    fromSideEffectWithLog(s"Selecting value: $value in select element: ${s.by}", _ => s.select.selectByValue(value))

  def switchToMainWindow: Action[Unit] =
    fromSideEffectWithLog("Switching to main window", { d =>
      d.d.switchTo().defaultContent()
      ()
    })

  def waitForElementToNotExistBy(by: By): Action[Unit] =
    waitForActionToReturnTrue(exists(by).not)


  def waitForElementToBecomeVisibleBy(by: By): Action[Unit] =
    waitForActionToReturnTrue(isDisplayedBy(by))

  def waitForElementToBecomeSelectedBy(by: By): Action[Unit] =
    waitForActionToReturnTrue(isSelectedBy(by))

  def waitForElementToBecomeEnabledBy(by: By): Action[Unit] =
    waitForActionToReturnTrue(isEnabledBy(by))


  def waitForElementToBecomeNotVisibleBy(by: By): Action[Unit] =
    waitForActionToReturnTrue(isDisplayedBy(by).not)

  def waitForElementToBecomeNotSelectedBy(by: By): Action[Unit] =
    waitForActionToReturnTrue(isSelectedBy(by).not)

  def waitForElementToBecomeNotEnabledBy(by: By): Action[Unit] =
    waitForActionToReturnTrue(isEnabledBy(by).not)


  /**
    * Wait for a condition, while it succeeds with false, until it succeeds with true.
    *
    * The condition is an Action[Boolean]
    * - the condition must succeed with true for waitFor(condition) to succeed
    * - if the condition succeeds with false, it is polled until timeout
    * - if the condition fails, or does not succeed with true, waitFor(condition) fails
    */
  def waitForActionToReturnTrue(condition: Action[Boolean]): Action[Unit] =
    Waiter.waitForActionToReturnTrue(condition, WaitOptions.default)

  def waitForActionToReturnTrueWithWaitOptions(condition: Action[Boolean], wo: WaitOptions): Action[Unit] =
    Waiter.waitForActionToReturnTrue(condition, wo)

  /**
    * Wait for a condition
    * The condition is an Action[T]
    * - the condition must succeed for waitFor(condition) to succeed
    * - if the condition fails, waitFor(condition) polls until timeout
    */
  def waitForActionToSucceed[T](condition: Action[T]): Action[T] =
    waitForActionToSucceedWithWaitOptions(condition, WaitOptions.default)

  def waitForActionToSucceedWithWaitOptions[T](condition: Action[T], wo: WaitOptions): Action[T] =
    Waiter.waitForActionToSucceed(condition, wo)

  def sleep(millis: Long): Action[Unit] =
    fromSideEffectWithLog(s"Sleeping for $millis milliseconds", _ => Thread.sleep(millis))

  def quit: Action[Unit] =
    fromSideEffectWithLog("Quitting", _.d.quit())

  def getAttribute(e: Elem, attribute: String): Action[Option[String]] =
    fromSideEffectWithLog(s"""Getting attribute "$attribute" from element: ${e.by}""", _ => Option(e.e.getAttribute(attribute)))

  def getAttributeBy(by: By, attribute: String): Action[Option[String]] =
    findAnd(getAttribute(_, attribute), by)

  def getAttributeOrEmptyString(e: Elem, attribute: String): Action[String] =
    for {
      v <- getAttribute(e, attribute)
    } yield v.getOrElse("")

  def getAttributeOrEmptyStringBy(by: By, attribute: String): Action[String] =
    findAnd(getAttributeOrEmptyString(_, attribute), by)

  private def izz(e: Elem, name: String, f: Elem => Boolean): Action[Boolean] =
    for {
      b <- fromSideEffectWithLog(s"Checking if element is $name: ${e.by}", _ => f(e))
      _ <- Action.log(s"Element ${if (b) "IS" else "IS NOT"} $name: ${e.by}")
    } yield b

  def isSelected(e: Elem): Action[Boolean] =
    izz(e, "selected", _.e.isSelected)

  def isEnabled(e: Elem): Action[Boolean] =
    izz(e, "enabled", _.e.isEnabled)

  def isDisplayed(e: Elem): Action[Boolean] =
    izz(e, "displayed", _.e.isDisplayed)


  def isSelectedBy(by: By): Action[Boolean] =
    findAnd(isSelected, by)

  def isEnabledBy(by: By): Action[Boolean] =
    findAnd(isEnabled, by)

  def isDisplayedBy(by: By): Action[Boolean] =
    findAnd(isDisplayed, by)


  def select(e: Elem): Action[Unit] =
    click(e) onlyIfA isSelected(e).not

  def deselect(e: Elem): Action[Unit] =
    click(e) onlyIfA isSelected(e)

  def selectBy(by: By): Action[Unit] =
    findAnd(select, by)

  def deselectBy(by: By): Action[Unit] =
    findAnd(deselect, by)

  def close: Action[Unit] =
    fromSideEffectWithLog("Closing browser", _.d.close())

  def back: Action[Unit] =
    navigate("Back", _.back())

  def forward: Action[Unit] =
    navigate("Forward", _.forward())

  def refresh: Action[Unit] =
    navigate("Refresh", _.refresh())

  private def navigate(log: String, f: Navigation => Unit): Action[Unit] = {
    fromSideEffectWithLog(log, d => f(d.d.navigate()))
  }

  def addCookie(cookie: Cookie): Action[Unit] =
    manage("Add cookie: " + cookie, _.addCookie(cookie))

  def deleteCookieNamed(name: String): Action[Unit] =
    manage(s"Delete cookie named: $name", _.deleteCookieNamed(name))

  def deleteCookie(c: Cookie): Action[Unit] =
    manage(s"Delete cookie $c", _.deleteCookie(c))

  def deleteAllCookies(): Action[Unit] =
    manage(s"Delete all cookies", _.deleteAllCookies())

  def getCookies: Action[List[Cookie]] =
    manage("Get cookies", x => asScalaSet(x.getCookies).toList)

  def getCookieNamedOpt(name: String): Action[Option[Cookie]] =
    manage(s"Get cookie named $name", x => Option.apply(x.getCookieNamed(name)))

  def getCookieNamed(name: String): Action[Cookie] =
    getCookieNamedOpt(name).onlyIfSomeWithLog(s"Could not find cookie named $name")

  private def manage[T](log: String, f: Options => T): Action[T] =
    fromSideEffectWithLog(log, d => f(d.d.manage()))

  // TODO: Better type for the args. Make a typesafe marshalling API
  def executeScript(script: String, args: List[Object]): Action[Object] =
    onJavascriptExecutor("Execute script", _.executeScript(script, args: _*))

  // TODO: Better type for the args. Make a typesafe marshalling API
  def executeAsyncScript(script: String, args: List[Object]): Action[Object] =
    onJavascriptExecutor("Execute async script", _.executeAsyncScript(script, args: _*))

  private def onJavascriptExecutor[T](log: String, f: JavascriptExecutor => T): Action[T] =
    fromSideEffectWithLog(log, { d =>
      val wd: WebDriver = d.d
      // Not the safest, but all bar "UnimplementedWebDriver" are subclasses of JavaScriptExecutor
      // and fromSideEffectWithLog will catch any exceptions.
      val jse: JavascriptExecutor = wd.asInstanceOf[JavascriptExecutor]
      f(jse)
    })
}