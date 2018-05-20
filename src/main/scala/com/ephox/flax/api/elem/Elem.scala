package com.ephox.flax
package api.elem

import com.ephox.flax.api.action.Action
import org.openqa.selenium._

/**
  * Wrapper around [[WebElement]].
  */
final case class Elem private(private[flax] val e: WebElement, by: By) {
  private def zingo[T](s: String, effect: WebElement => T): Action[T] =
    Action.fromSideEffectWithLog(s + " element: " + by, _ => effect(e))

  def click(): Action[Unit] =
    zingo("Clicking", _.click())

  def submit(): Action[Unit] =
    zingo("Submitting", _.submit())

  def sendKeys(var1: List[CharSequence]): Action[Unit] =
    zingo("Sending keys " + var1 + " to", _.sendKeys(var1: _*))

  def clear(): Action[Unit] =
    zingo("Clearing", _.clear())

  def getTagName: Action[String] =
    zingo("Getting tag name of", _.getTagName())

  def getAttribute(var1: String): Action[String] =
    zingo(s"""Getting attribute "$var1" of""", _.getAttribute(var1))

  def isSelected: Action[Boolean] =
    zingo("Getting selection state of", _.isSelected())

  def isEnabled: Action[Boolean] =
    zingo("Getting enabled state of", _.isEnabled())

  def getText: Action[String] =
    zingo("Getting text of", _.getText())

  def isDisplayed: Action[Boolean] =
    zingo("Getting displayed state of", _.isDisplayed())

  def getLocation: Action[Point] =
    zingo("Getting location of", _.getLocation())

  def getSize: Action[Dimension] =
    zingo("Getting size of", _.getSize())

  def getRect: Action[Rectangle] =
    zingo("Getting rect of", _.getRect())

  def getCssValue(var1: String): Action[String] =
    zingo(s"""Getting css "$var1" value of""", _.getCssValue(var1))
}

object Elem {
  def elem(e: WebElement, by: By): Elem =
    Elem(e, by)
}