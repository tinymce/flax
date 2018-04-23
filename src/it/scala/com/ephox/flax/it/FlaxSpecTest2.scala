package com.ephox.flax.it

import com.ephox.flax.api.action.FlaxActions
import com.ephox.flax.api.elem.{Browser, Firefox}
import com.ephox.flax.api.specs2.FlaxSpec
import org.specs2.mutable.Specification

class FlaxSpecTest2 extends Specification with FlaxSpec {
  override def curBrowser: Browser = Firefox

  // NOTE: These two tests: FlaxSpecTest1 and FlaxSpecTest2
  // are designed to ensure that the FlaxSpec lifecycle works
  // when using the driver from multiple test files. Do
  // not combine them unless you are sure FlaxSpec's lifecycle
  // is still tested elsewhere (e.g. via AWebTest)
  "Delete Cookies from Test2" should {
    "test2.deleteCookies" in {
      for {
        _ <- FlaxActions.deleteAllCookies()
      } yield ()
    }
  }
}