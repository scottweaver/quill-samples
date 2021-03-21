package samples

import org.scalatest.funspec._
import java.time.Instant
import ExtendedQuillContextTest._
import org.scalatest.Inside
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Date
import QuillInstances._
import org.scalatest.matchers.should.Matchers

class ExtendedQuillContextTest extends AnyFunSpec with Matchers {

  import context._

  describe("ExtendedQuillContext") {

    it("Should properly encode a Timeframe filter.") {
      val low1 = LocalDateTime.of(2021, 1, 1, 0, 0, 0).toInstant(ZoneOffset.UTC)
      val high1 = LocalDateTime.of(2021, 2, 2, 0, 0, 0).toInstant(ZoneOffset.UTC)
      val low2 = LocalDateTime.of(2021, 3, 3, 0, 0, 0).toInstant(ZoneOffset.UTC)
      val high2 = LocalDateTime.of(2021, 4, 4, 0, 0, 0).toInstant(ZoneOffset.UTC)

      val tf1 = Timeframe.ForTimes(low1, high1)
      val tf2 = Timeframe.ForTimes(low2, high2)

      val q = context.quote {
        context.query[DateTable].filter { t =>
          (t.date1 in lift( tf1 )) &&
          (t.date2 in lift( tf2 ))
        }
      }
     
      val q2 = context.quote {
        context.query[DateTable] filter { t =>
          (t.date1 inRange lift( tf1.timeRange )) &&
          (t.date2 inRange lift( tf2.timeRange ))
        }
      }
      val result = context.run(q)
      val result2 = context.run(q2)

      val actualStatement = result.string
      info(actualStatement)
      val expectedStatement =
        "SELECT t.date1, t.date2 FROM date_table t WHERE (t.date1 >= ? OR t.date1 <= ?) AND (t.date2 >= ? OR t.date2 <= ?)"

      actualStatement shouldBe expectedStatement
      result2.string shouldBe expectedStatement

      val row: PrepareRow = result.prepareRow

      // Verify all dates are in the correct positions.
      Inside.inside(row.data.toList) {
        case (actualLow1Date: Date) :: (actualHigh1Date: Date) :: (actualLow2Date: Date) :: (actualHigh2Date: Date) :: Nil =>
          actualLow1Date.getTime shouldBe low1.toEpochMilli()
          actualHigh1Date.getTime shouldBe high1.toEpochMilli()
          actualLow2Date.getTime shouldBe low2.toEpochMilli()
          actualHigh2Date.getTime shouldBe high2.toEpochMilli()
      }

      result.prepareRow shouldBe result2.prepareRow

    }
  }

}

object ExtendedQuillContextTest {
  import io.getquill._

  final case class DateTable(date1: Instant, date2: Instant)

  val context = new SqlMirrorContext(MySQLDialect, SnakeCase) with ExtendedQuillContext

}
