package samples

import java.time.Instant
import Timeframe._

sealed trait Timeframe {

  val timeRange: (Instant, Instant) = this match {
    case ForTimes(from, to) => (from, to)
  }
}

object Timeframe {

  final case class ForTimes(from: Instant, to: Instant) extends Timeframe
}
