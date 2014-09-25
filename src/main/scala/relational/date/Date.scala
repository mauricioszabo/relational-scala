package relational.date

import java.util.{Calendar => C, GregorianCalendar => GC, TimeZone => TZ}
import scala.math.ScalaNumericAnyConversions

case class Date(timezone: TZ,
    year: Int,month: Int,day: Int,
    hour: Int,minute: Int,second: Int,nanosecond: Long)
    extends Ordered[Date] {

  def this(year: Int=0, month: Int=1, day: Int=1,
      hour: Int=0, minute: Int=0, second: Int=0, nanosecond: Long=0)
      (implicit timezone: TZ = null) = this(
    if(timezone == null) new GC().getTimeZone else timezone,
    year, month, day, hour, minute, second, nanosecond)

  def compare(other: Date) = timestamp compareTo other.timestamp

  private val stringForm = "%04d-%02d-%02d %02d:%02d:%02d.%d" format
    (year, month, day, hour, minute, second, nanosecond)

  val timestamp = java.sql.Timestamp.valueOf(stringForm)

  override def toString = "Date ("+ stringForm +")"
}

object Date {
  private val date = "(\\d{4})-(\\d{1,2})-(\\d{1,2})".r
  private val time = "(\\d{1,2}):(\\d{1,2})".r
  private val timeWithSeconds = (time + ":(\\d{1,2})").r
  private val timeWithNano = (timeWithSeconds + "\\.(\\d+)").r
  private val dateTime = (date + " .*").r

  def today = {
    val today = new java.util.Date
    val time = new java.sql.Timestamp(today.getTime)
    parse(time.toString)
  }

  def parse(string: String)(implicit t: TZ = null) = string match {
    case dateTime(day,month,year) => createDateTimeFrom(string)(t)
    case date(y,m,d) => new Date(y.toInt,m.toInt,d.toInt)(t)
    case time(h,m) => new Date(hour=h.toInt, minute=m.toInt)(t)
    case timeWithSeconds(h,m,s) => new Date(hour=h.toInt, minute=m.toInt, second=s.toInt)(t)
    case timeWithNano(h,m,s,n) =>
      new Date(hour=h.toInt, minute=m.toInt, second=s.toInt, nanosecond=n.toLong)(t)
  }

  private def createDateTimeFrom(string: String)(t: TZ) = {
    val dateTime(syear,smonth,sday) = string
    val (year, month, day) = (syear.toInt, smonth.toInt, sday.toInt)

    val onlyTime = (date + " ").r.replaceFirstIn(string, "")

    onlyTime match {
      case timeWithNano(h,m,s,n) => new Date(year,month,day,h.toInt,m.toInt,s.toInt,n.toInt)(t)
      case timeWithSeconds(h,m,s) => new Date(year,month,day,h.toInt,m.toInt,s.toInt)(t)
      case time(h,m) => new Date(year,month,day,h.toInt,m.toInt)(t)
    }
  }
}

//package {
//  implicit def num2datelike[A <% ScalaNumericAnyConversions]
//}
