package com.ephox.flax

import com.ephox.flax.api.action.{Err, Log}
import com.ephox.flax.api.elem.Driver
import scalaz.effect.IO
import scalaz.{EitherT, ReaderT, WriterT}

package object internal {

  private[flax] type RT[A] = ReaderT[IO, Driver, A]
  private[flax] type WT[A] = WriterT[RT, Log[String], A]
  private[flax] type ActionBase[A] = EitherT[WT, Err, A]
}
