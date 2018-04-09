package com.ephox.flax.api

import com.ephox.flax.api.elem.Driver

import scalaz.{EitherT, ReaderT, WriterT}
import scalaz.effect.IO

package object action {

  private[flax] type RT[A] = ReaderT[IO, Driver, A]
  private[flax] type WT[A] = WriterT[RT, Log[String], A]
  private[flax] type ActionBase[A] = EitherT[WT, Err, A]
}
