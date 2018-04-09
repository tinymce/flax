package com.ephox.flax
package tests

import api.action.Action._
import api.action.Log, Log._
import api.action.Node
import misc.TestUtils._

import org.specs2.ScalaCheck
import org.specs2.mutable.Specification
import org.specs2.scalaz.ScalazMatchers
import scalaz.DList
import scalaz.std.string._
import scalaz.syntax.monad._

class NestingTest extends Specification with ScalaCheck with ScalazMatchers {

  "nesting" should {
    "nested mute action" in prop { (s: String) =>
      runAndGetLogs(nested(s, noop)) must equal(single(s))
    }

    "nested action with one message" in prop { (s: String, n: String) =>
      runAndGetLogs(nested(s, noop.setLog(single(n)))) must equal(singleNode(Node(s, single(n))))
    }

    "nested action with two messages" in prop { (a: String, b: String, c: String) =>
      runAndGetLogs(nested(a, logOnly(b) >> logOnly(c))) must equal(singleNode(Node(a, Log.fromDList(DList(b, c)))))
    }
  }
}
