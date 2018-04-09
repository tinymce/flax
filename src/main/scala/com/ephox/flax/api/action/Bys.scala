package com.ephox.flax
package api.action

import java.util

import org.openqa.selenium.support.pagefactory.ByChained
import org.openqa.selenium.{By, SearchContext, WebElement}

import scala.collection.JavaConverters._

/**
  * Some extra combinators for producing Selenium [[By]] values.
  *
  * No effect-tracking done here - it's expected that these values are only called by
  * effect-tracked operations in [[FlaxActions]].
  */
object Bys {

  def chained(ancestors: By, descendents: By): By =
    new ByChained(ancestors, descendents)

  def cssProperty(selector: By, cssProperty: String, cssValue: String): By =
    cssPropertyMatching(selector, cssProperty, _ == cssValue)

  def cssPropertyMatching(selector: By, cssProperty: String, predicate: String => Boolean): By =
    by(_.findElements(selector).asScala.filter(v => predicate(v.getCssValue(cssProperty))).asJava)

  /**
    * Finds all elements that match either By.
    * May produce duplicates.
    */
  def or(a: By, b: By): By =
    by(s => (s.findElements(a).asScala ++ s.findElements(b).asScala).asJava)

  private def by(f: SearchContext => util.List[WebElement]): By = new By {
    override def findElements(context: SearchContext): util.List[WebElement] = f(context)
  }
}
