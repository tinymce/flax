package com.ephox.flax.webtests

import java.io.File

import com.ephox.flax.api.action.SeleniumActions
import com.ephox.flax.api.elem.Browser
import com.ephox.flax.api.specs2.Flax
import org.specs2.mutable.Specification

class AWebTest extends Specification with Flax {

  override def curBrowser: Browser = Browser.firefox

  "flaxo" >> {

    println(System.getProperty("webdriver.gecko.driver"))

    for {
      _ <- SeleniumActions.get("https://tinymce.com")
    } yield ()
  }
}
