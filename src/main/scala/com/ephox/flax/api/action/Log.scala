package com.ephox.flax
package api.action

import PPrint._

import scalaz.DList._
import scalaz.Equal.{equal, equalBy}
import scalaz.std.list._
import scalaz.Show.shows
import scalaz.std.string.stringInstance
import scalaz.syntax.equal._
import scalaz.syntax.show._
import scalaz.syntax.foldable.ToFoldableOps
import scalaz.{DList, Equal, Monoid, Show}


/**
  * A tree structure that starts with a list of Nodes.
  *
  * @param list nodes
  * @tparam A payload type
  */
final case class Log[A](list: DList[Node[A]]) {
  def map[B](f: A => B): Log[B] =
    Log(list.map(_.map(f)))

  def :+(a: A): Log[A] =
    Log(list :+ Node[A](a))

  def +:(a: A): Log[A] =
    Log(Node[A](a) +: list)

  def ++(other: Log[A]): Log[A] =
    Log(list ++ other.list)

  override def equals(obj: scala.Any): Boolean =
    throw new UnsupportedOperationException("Use scalaz === instead")
}

object Log {
  implicit def logMonoid[T]: Monoid[Log[T]] = new Monoid[Log[T]] {
    override def zero: Log[T] =
      empty

    override def append(left: Log[T], right: => Log[T]): Log[T] =
      left ++ right
  }

  implicit def logEqual[T : Equal]: Equal[Log[T]] =
    equalBy(_.list)

  implicit def logPPrint[T : PPrint]: PPrint[Log[T]] = PPrint { log =>
    def toLines(log: Log[T]): DList[String] =
      log.list flatMap (n => ("- " + n.label.pprint) +: indent(toLines(n.children)))

    def indent(list: DList[String]): DList[String] =
      list map ("  " + _)

    toLines(log).intercalate("\n")
  }

  implicit def logShow[T : Show]: Show[Log[T]] = shows { log =>
    "Log(" + log.list.toList.show + ")"
  }

  def fromDList[T](list: DList[T]): Log[T] =
    Log[T](list map (Node[T](_)))

  def single[T](t: T): Log[T] =
    fromDList(DList(t))

  def empty[T]: Log[T] =
    fromDList(DList())

  def singleNode[T](nt: Node[T]): Log[T] =
    Log(DList(nt))

  override def equals(obj: scala.Any): Boolean =
    throw new UnsupportedOperationException("Use scalaz === instead")
}

final case class Node[A](label: A, children: Log[A] = Log(DList[Node[A]]())) {
  def map[B](f: A => B): Node[B] = Node(f(label), children.map(f))
}

object Node {
  implicit def nodeEqual[T: Equal]: Equal[Node[T]] = equal { (a, b) =>
    a.label === b.label && a.children === b.children
  }

  implicit def nodeShow[T: Show]: Show[Node[T]] = shows { node =>
    "Node(" + node.label.shows + "," + node.children.shows + ")"
  }
}