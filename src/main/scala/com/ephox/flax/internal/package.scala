package com.ephox.flax

import com.ephox.flax.api.action.{Err, Log}
import com.ephox.flax.api.elem.Driver
import scalaz.effect.IO
import scalaz.{EitherT, ReaderT, WriterT}

package object internal {
  private[flax] type ActionBase[A] = EitherT[WriterT[ReaderT[IO, Driver, ?], Log[String], ?], Err, A]
}
