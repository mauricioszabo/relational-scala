package relational.comparissions

import relational.attributes._
import relational.PartialStatement

class In(attribute: AttributeLike, list: Seq[Any]) extends Comparission {
  lazy val partial = {
    val ap = attribute.partial
    val query = ap.query + " IN (" +
      list.map { e => "?" }.mkString(",") + ")"
    new PartialStatement(query, ap.attributes ++ list)
  }
}
