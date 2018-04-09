package com.ephox.flax.it

import com.ephox.flax.api.action.FlaxActions
import com.ephox.flax.api.elem.Browser
import com.ephox.flax.api.specs2.FlaxSpec
import org.specs2.mutable.Specification

class AWebTest extends Specification with FlaxSpec {

  override def curBrowser: Browser = Browser.firefox

  "flaxo" >> {
    FlaxActions.get("https://tinymce.com")
  }
}
