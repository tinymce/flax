package com.ephox.flax
package internal

import scalaz.std.option.{none, some}

object OptionUtils {

  def not[T](ot: Option[T]): Option[Unit] =
    ot.fold(some(()))(_ => none)

}
