package com.ephox.flax.it

import com.ephox.flax.api.action.Action, Action._
import com.ephox.flax.api.action.FlaxActions._
import com.ephox.flax.api.elem.Browser
import com.ephox.flax.api.specs2.FlaxSpec
import org.openqa.selenium.By.{linkText, partialLinkText}
import org.specs2.mutable.Specification
import scalaz.syntax.monad._

class AWebTest extends Specification with FlaxSpec {

  override def curBrowser: Browser = Browser.firefox

  "Flax github" >> {
    for {
      _ <- get("https://github.com/ephox/flax/")
      _ <- clickBy(linkText("geckodriver"))
      _ <- nested("silly navigations", back *> forward *> back *> refresh)
      _ <- clickBy(partialLinkText("ExampleTestBase"))
    } yield ()
  }
}
