package com.ephox.flax
package api.action

import api.elem.Elem

import org.openqa.selenium.By
import scalaz.\/, \/._

sealed trait Err

final case class AssertionFailed(message: String) extends Err

final case class CouldNotFindElement(by: By) extends Err

// FIX: add a By param here?
final case class WrongElementType() extends Err

final case class Kersploded(e: Throwable) extends Err

final case class Other(message: String) extends Err


object Err {
  def assertionFailed(message: String): Err = AssertionFailed(message)

  def couldNotFindElement(by: By): Err = CouldNotFindElement(by)

  def wrongElementType(e: Throwable): Err = WrongElementType()

  def kersploded(e: Throwable): Err = Kersploded(e)

  def other(s: String): Err = Other(s)
}

object ErrOrElem {

  def fromOption(err: Err)(o: Option[Elem]): Err \/ Elem =
    o.fold(left[Err, Elem](err))(right)
}
