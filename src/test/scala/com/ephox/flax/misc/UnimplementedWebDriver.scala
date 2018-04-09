package com.ephox.flax
package misc

import Predef.???
import java.util

import org.openqa.selenium.WebDriver._
import org.openqa.selenium._

//noinspection NotImplementedCode
object UnimplementedWebDriver extends WebDriver {
  override def getPageSource: String = ???

  override def findElements(by: By): util.List[WebElement] = ???

  override def getWindowHandle: String = ???

  override def get(url: String): Unit = ???

  override def manage(): Options = ???

  override def getWindowHandles: util.Set[String] = ???

  override def switchTo(): TargetLocator = ???

  override def close(): Unit = ???

  override def quit(): Unit = ???

  override def getCurrentUrl: String = ???

  override def navigate(): Navigation = ???

  override def getTitle: String = ???

  override def findElement(by: By): WebElement = ???
}
