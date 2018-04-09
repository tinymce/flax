package com.ephox.flax
package internal

import scalaz.std.option._

object ListUtils {

  def firstOpt[A, B](list: List[A])(f: A => Option[B]): Option[B] =
    list.foldLeft(none[B]) { case (ob, a) => ob orElse f(a) }
}
