package com.ephox.flax
package tests

import com.ephox.flax.api.action.{Action, Err, Log}, Log._, Err._
import com.ephox.flax.api.action.Action.point
import org.specs2.ScalaCheck
import org.specs2.mutable.Specification
import com.ephox.flax.misc.TestUtils._
import org.specs2.scalaz.ScalazMatchers
import scalaz._
import scalaz.std.anyVal._
import scalaz.std.string._
import scalaz.std.tuple._
import scalaz.syntax.monad._

class ActionTest extends Specification with ScalaCheck with ScalazMatchers {

  def ⊥[A]: A = sys.error("⊥")

  "Action" should {
    "anon bind" in prop { (i: Int, j: Int) =>
      runAndGetValue(point(i) >> point(j)) must_=== \/-(j)
    }

    "bind" in prop { (i: Int, j: Int) =>
      runAndGetValue(point(i) >>= (i => point((i, j)))) must_=== \/-((i, j))
    }

    "fromSideEffectWithLog" >> {
      "with pure value" >> prop { (s: String, i: Int) =>
        Action.fromSideEffectWithLog(s, _ => i).run.run.run.run(null).unsafePerformIO() must equal((Log.single(s), \/.right[Err, Int](i)))
      }
    }
  }
}
