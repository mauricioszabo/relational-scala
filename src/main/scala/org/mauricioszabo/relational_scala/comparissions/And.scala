package relational.comparissions

import relational.PartialStatement

class And(comparissions: List[Comparission]) extends Comparission {
  lazy val partial = {
    val partials = comparissions.view.map { _.partial }
    val query = "(" + partials.map { p => p.query }.mkString(" AND ") + ")"
    val attributes = partials.flatMap { _.attributes }
    new PartialStatement(query, attributes)
  }
}

