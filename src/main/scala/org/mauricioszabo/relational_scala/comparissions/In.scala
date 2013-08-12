package org.mauricioszabo.relational_scala.comparissions

import org.mauricioszabo.relational_scala.attributes._
import org.mauricioszabo.relational_scala.PartialStatement

class In(attribute: AttributeLike, list: Seq[Any]) extends Comparission {
  lazy val partial = {
    val ap = attribute.partial
    val query = ap.query + " IN (" +
      list.map { e => "?" }.mkString(",") + ")"
    new PartialStatement(query, ap.attributes ++ list)
  }
}
