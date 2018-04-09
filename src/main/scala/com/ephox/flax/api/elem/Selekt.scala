package com.ephox.flax
package api.elem

import org.openqa.selenium.By
import org.openqa.selenium.support.ui.Select

/**
  * Wrapper around [[Select]].
  * Doesn't expose any methods - use [[com.ephox.flax.api.action.FlaxActions]]
  */
final case class Selekt private (private[flax] val select: Select, by: By)

object Selekt {
  def selekt(select: Select, by: By): Selekt =
    Selekt(select, by)
}
