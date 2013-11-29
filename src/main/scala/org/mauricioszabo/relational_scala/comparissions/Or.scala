package org.mauricioszabo.relational_scala.comparissions

import org.mauricioszabo.relational_scala.PartialStatement

class Or(protected val comparissions: List[Comparission]) extends Comparission {
  lazy val partial = {
    val partials = comparissions.view.map { _.partial }
    val query = "(" + partials.map { p => p.query }.mkString(" OR ") + ")"
    val attributes = partials.flatMap { _.attributes }
    new PartialStatement(query, attributes)
  }

  def equivalentTo(other: Or) = {
    if(comparissions.size != other.comparissions.size)
      false
    else { false }
  }
}
