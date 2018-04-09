package com.ephox.flax
package api.elem

import org.openqa.selenium.{By, WebElement}

/**
  * Wrapper around [[WebElement]].
  * Doesn't expose any methods - use [[com.ephox.flax.api.action.SeleniumActions]]
  */
final case class Elem private (private[flax] val e: WebElement, by: By)

object Elem {
  def elem(e: WebElement, by: By): Elem =
    Elem(e, by)
}