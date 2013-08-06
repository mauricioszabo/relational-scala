package org.mauricioszabo.relational_scala.comparissions

import org.mauricioszabo.relational_scala.attributes._
import org.mauricioszabo.relational_scala.PartialStatement

class In(attribute: Attribute, list: Seq[Any]) extends Comparission {
  lazy val partial = {
    val query = attribute.representation + " IN (" +
      list.map { e => "?" }.mkString(",") + ")"
    new PartialStatement(query, list)
  }
}
