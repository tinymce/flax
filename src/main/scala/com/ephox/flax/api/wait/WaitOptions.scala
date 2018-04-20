package com.ephox.flax.api.wait

final case class WaitOptions(timeoutMillis: Long, pollDelayMillis: Long) {
  def setTimeoutMillis(newTimeoutMillis: Long): WaitOptions = copy(timeoutMillis = newTimeoutMillis)
  def setPollDelay(newPollDelayMillis: Long): WaitOptions = copy(pollDelayMillis = newPollDelayMillis)
}

object WaitOptions {
  val default = WaitOptions(timeoutMillis = 20000, pollDelayMillis = 500)
}
