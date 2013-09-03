import org.scalatest._
import org.mauricioszabo.relational_scala.date._

class DateTest extends WordSpec with matchers.ShouldMatchers {
  val oneHour = 1 * 1000 * 60 * 60
  implicit val timezone = new java.util.SimpleTimeZone(oneHour, "GMT +1")
  val date = new Date(year=2010,month=1,day=10)

  "Date" should {
    "be constructed by a string" in {
      val result = Date.parse("2010-01-10")
      date should be === result
    }

    "be constructed with ANY string" in {
      Date.parse("2010-01-1") should be === Date.parse("2010-01-1 00:00")
      Date.parse("00:10") should be === Date.parse("0:10:0")
      Date.parse("00:10:20") should be === Date.parse("0:10:20.0")
    }

    "get today's hour" in {
      Date.today should be > date
    }

    "represent an Java SQL Timestamp (obeying TimeZone)" in {
      pending
      val timestamp = java.sql.Timestamp.valueOf("2010-01-9 23:0:0")
      date.timestamp should be === timestamp
    }

    "subtract from another date" in {
      pending
      //val result = date + 1.day
      //result should be === Date.parse("2010-01-09")
    }
  }
}
