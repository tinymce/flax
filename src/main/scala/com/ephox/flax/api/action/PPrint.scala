package com.ephox.flax
package api.action

import Predef.identity

/** trait for pretty-printing objects as strings.
  * Why not "Show"? Show is more for debugging, whereas this is for reporting test results.
  * Also scalaz's Show[String] outputs strings in quotes e.g. {{{"s"}}} which is unsuitable.
  */
trait PPrint[T] {
  def pprint(t: T): String
}

/** Wrapper allowing x.pprint to be called.
  * {{{import PPrint.toPPrintOps}}} to get the implicit conversion to PPrintOps
  */
final case class PPrintOps[T](t: T)(implicit ev: PPrint[T]) {
  def pprint: String = ev.pprint(t)
}

object PPrint {
  def apply[T](f: T => String): PPrint[T] = new PPrint[T] {
    override def pprint(t: T): String = f(t)
  }

  implicit def pprintString: PPrint[String] = PPrint(identity)

  implicit def toPPrintOps[T](t : T)(implicit ev: PPrint[T]): PPrintOps[T] =
    PPrintOps(t)
}


