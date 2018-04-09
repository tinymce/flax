package com.ephox.flax
package internal

import java.util

import scalaz.syntax.std.boolean.ToBooleanOpsFromBoolean

object JuListUtils {
  def headOption[T](l: util.List[T]): Option[T] =
    (!l.isEmpty) option l.get(0)
}
