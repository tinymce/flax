package com.ephox.flax.it

import com.ephox.flax.api.action.Action._
import com.ephox.flax.api.action.FlaxActions._
import com.ephox.flax.api.elem.Browser
import com.ephox.flax.api.specs2.{FlaxAssertions, FlaxSpec}
import org.openqa.selenium.By
import org.specs2.mutable.Specification

class TestSiteTest extends Specification with FlaxSpec {

  override def curBrowser: Browser = Browser.chrome

  "Flax github" >> {
    for {
      _ <- get("http://htmlpreview.github.io/?https://github.com/ephox/flax/blob/master/src/it/resources/com/ephox/flax/it/testsite/index.html")
      _ <- nested("fill out form", for {
        _ <- setTextBy(By.name("name"), "Bob")
        _ <- clickBy(By.id("submit"))
      } yield ())
      _ <- FlaxAssertions.assertText(By.id("messages"), "Thanks for signing up, Bob")
    } yield ()
  }
}
