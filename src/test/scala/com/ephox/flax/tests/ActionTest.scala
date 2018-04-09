package com.ephox.flax
package tests

import com.ephox.flax.api.action.Action.point
import org.specs2.ScalaCheck
import org.specs2.mutable.Specification
import com.ephox.flax.misc.TestUtils._
import org.specs2.scalaz.ScalazMatchers
import scalaz.\/-
import scalaz.syntax.monad._

class ActionTest extends Specification with ScalaCheck with ScalazMatchers {
  "Action" should {
    "anon bind" in prop { (i: Int, j: Int) =>
      runAndGetValue(point(i) >> point(j)) must_=== \/-(j)
    }

    "bind" in prop { (i: Int, j: Int) =>
      runAndGetValue(point(i) >>= (i => point((i, j)))) must_=== \/-((i, j))
    }
  }
}
