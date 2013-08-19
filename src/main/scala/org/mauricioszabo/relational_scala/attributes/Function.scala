package org.mauricioszabo.relational_scala.attributes

import org.mauricioszabo.relational_scala.comparissions._
import org.mauricioszabo.relational_scala.PartialStatement

class Function(function: String, attribute: AttributeLike*) extends Comparable {
  lazy val partial = {
    var queries: List[String] = Nil
    var attributes: Seq[Any] = Nil
    val partials = attribute.map { e =>
      val p = e.partial
      queries = queries :+ p.query
      attributes = attributes ++ p.attributes
    }

    val query = function + "(" + queries.mkString(", ") + ")"
    new PartialStatement(query, attributes)
  }
}
