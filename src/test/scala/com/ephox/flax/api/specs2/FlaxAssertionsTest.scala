package com.ephox.flax.api.specs2

import org.specs2.mutable.Specification

class FlaxAssertionsTest extends Specification {

  "Assertions do not kaboom" >> {
    FlaxAssertions.assert(3 must_=== 4).attempt.runOrThrow(null).isLeft must_=== true
  }
}
