package com.ephox.flax
package api.specs2

import com.ephox.flax.api.action.FlaxActions
import com.ephox.flax.api.elem.{Browser, Firefox}
import org.specs2.mutable.Specification

class FlaxSpecTest1 extends Specification with FlaxSpec {
  override def curBrowser: Browser = Firefox

  // NOTE: These two tests: FlaxSpecTest1 and FlaxSpecTest2
  // are designed to ensure that the FlaxSpec lifecycle works
  // when using the driver from multiple test files. Do
  // not combine them unless you are sure FlaxSpec's lifecycle
  // is still tested elsewhere (e.g. via AWebTest)
  "Delete Cookies from Test1" should {
    "test1.deleteCookies" in {
      for {
        _ <- FlaxActions.deleteAllCookies()
      } yield ()
    }
  }
}