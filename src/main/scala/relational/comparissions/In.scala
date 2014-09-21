package relational.comparissions

import relational.Adapter
import relational.attributes._
import relational.PartialStatement

case class In(attribute: AttributeLike, list: Seq[Any]) extends Comparission {
  lazy val partial = {
    val ap = attribute.partial
    def sql(a: Adapter) = ap.sql(a) + " IN (" +
      list.map { e => "?" }.mkString(",") + ")"
    new PartialStatement(ap.attributes ++ list)(sql)
  }
}
