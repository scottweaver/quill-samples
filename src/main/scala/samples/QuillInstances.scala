package samples

import io.getquill._
import java.util.Date
import java.time.Instant
import io.getquill.context.Context

trait QuillInstances {

  implicit val InstantEncoding: MappedEncoding[Instant, Date] =
    MappedEncoding(Date.from)

  implicit val InstantDecoding: MappedEncoding[Date, Instant] =
    MappedEncoding(_.toInstant)

}

object QuillInstances extends QuillInstances
