package relational.queries

import attr_helpers.{AttributesEntry, AttributesFromResultSet}
import relational._
import java.sql.ResultSet

class Query[A, U](protected[queries] val selector: Selector,
                  protected[queries] val resultStructure: AttributesEntry[A] => U)
                  extends Partial {

  val partial = selector.partial

  def asStream(fn: PartialStatement => ResultSet) = {
    val rs = executeQuery(fn)
    val entries = new AttributesFromResultSet(rs)

    def stream: Stream[U] =
      if(rs.next) resultStructure(entries) #:: stream
      else Stream.empty

    stream
  }

  private def executeQuery(fn: PartialStatement => ResultSet) = {
    fn(partial)
  }
}
