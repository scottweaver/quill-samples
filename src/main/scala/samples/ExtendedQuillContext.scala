package samples

import io.getquill.context.sql.SqlContext
import io.getquill._
import java.time.Instant
import doobie.quill.DoobieContext
import io.getquill.idiom.Idiom
import io.getquill.context.jdbc.MysqlJdbcContextBase

trait ExtendedQuillContext { self: SqlContext[_, _] =>

  /**
    * One MASSIVE hole in Quill is what appears to be an utter lack of
    * built-in support for the `java.time._` api.  So we need to do this
    * ourselves, unfortunately.
    *
    * @param left
    * @see [[https://github.com/getquill/quill/issues/677 Provide comparison operators for supported data types]]
    */
  implicit class InstantComparison(left: Instant) {
    def >(right: Instant) = quote(infix"$left > $right".as[Boolean])

    def <(right: Instant) = quote(infix"$left < $right".as[Boolean])

    def >=(right: Instant) = quote(infix"$left >= $right".as[Boolean])

    def <=(right: Instant) = quote(infix"$left <= $right".as[Boolean])
  }

  implicit class WithinTimeframe(left: Instant) {

    def in(timeframe: Timeframe) = {
      quote {
        val (low, high) = timeframe.timeRange
        infix"($left >= ${low} OR $left <= ${high})".as[Boolean]
      }
    }

    def inRange(timeframe: (Instant, Instant)) = {
      quote {
        infix"($left >= ${timeframe._1} OR $left <= ${timeframe._2})".as[Boolean]
      }
    }
  }

}

object ExtendedQuillContext {

  /**
    * Provides a Quill SqlContext, based off of [[DoobieContext.MySQL(SnakeCase)]]
    * with additional operations added to it.
    */
  def live = new DoobieContext.MySQL(SnakeCase) with ExtendedQuillContext

}
