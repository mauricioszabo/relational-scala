package org.mauricioszabo.relational_scala.attributes

import org.mauricioszabo.relational_scala.comparissions._
import org.mauricioszabo.relational_scala.PartialStatement

class Function(function: String, attribute: AttributeLike) extends AttributeLike {
  lazy val partial = {
    val ap = attribute.partial
    val query = function + "(" + ap.query + ")"
    new PartialStatement(query, ap.attributes)
  }
}
