package com.ephox.flax.it

import com.ephox.flax.api.action.{Action, FlaxActions}
import com.ephox.flax.api.elem.{Browser, Firefox}
import com.ephox.flax.api.specs2.FlaxSpec

trait ExampleTestBase extends FlaxSpec {
  override def curBrowser: Browser = Firefox

  override def beforeAllAction: Action[Unit] =
    Action.noop

  override def afterAllAction: Action[Unit] =
    FlaxActions.close
}
