package com.ephox.flax
package api.specs2

import com.ephox.flax.api.action.Action
import com.ephox.flax.api.action.SeleniumActions._
import com.ephox.flax.api.elem.Elem
import com.ephox.flax.api.specs2.Assertions._
import org.openqa.selenium.By

trait SeleniumAssertions {

  def assertAction(action: Action[Boolean]): Action[Unit] = for {
    b <- action
    _ <- assertTrue(b)
  } yield()


  def assertSelected(e: Elem): Action[Unit] =
    assertAction(isSelected(e))

  def assertEnabled(e: Elem): Action[Unit] =
    assertAction(isEnabled(e))

  def assertDisplayed(e: Elem): Action[Unit] =
    assertAction(isDisplayed(e))


  def assertNotSelected(e: Elem): Action[Unit] =
    assertAction(isSelected(e).not)

  def assertNotEnabled(e: Elem): Action[Unit] =
    assertAction(isEnabled(e).not)

  def assertNotDisplayed(e: Elem): Action[Unit] =
    assertAction(isDisplayed(e).not)


  def assertSelectedBy(by: By): Action[Unit] =
    assertAction(isSelectedBy(by))

  def assertEnabledBy(by: By): Action[Unit] =
    assertAction(isEnabledBy(by))

  def assertDisplayedBy(by: By): Action[Unit] =
    assertAction(isDisplayedBy(by))


  def assertNotSelectedBy(by: By): Action[Unit] =
    assertAction(isSelectedBy(by).not)

  def assertNotEnabledBy(by: By): Action[Unit] =
    assertAction(isEnabledBy(by).not)

  def assertNotDisplayedBy(by: By): Action[Unit] =
    assertAction(isDisplayedBy(by).not)


  def assertNotExistsNow(by: By): Action[Unit] =
    assertAction(exists(by).not)

  def assertExistsNow(by: By): Action[Unit] =
    assertAction(exists(by))

}

object SeleniumAssertions extends SeleniumAssertions
