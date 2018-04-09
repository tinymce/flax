package com.ephox.flax
package api.elem

sealed trait Browser

case object IE        extends Browser
case object Firefox   extends Browser
case object Chrome    extends Browser
case object Edge      extends Browser
case object Safari    extends Browser
case object PhantomJs extends Browser

object Browser {
  val ie       : Browser = IE
  val firefox  : Browser = Firefox
  val chrome   : Browser = Chrome
  val edge     : Browser = Edge
  val safari   : Browser = Safari
  val phantomJs: Browser = PhantomJs
}