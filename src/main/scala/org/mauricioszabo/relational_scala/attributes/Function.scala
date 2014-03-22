package relational.attributes

import relational.comparissions._
import relational.PartialStatement

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
