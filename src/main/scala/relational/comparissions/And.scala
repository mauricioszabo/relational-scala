package relational.comparissions

import relational.{Adapter, PartialStatement}

case class And(comparissions: Vector[Comparission]) extends Comparission {
  lazy val partial = {
    val partials = comparissions.view.map { _.partial }
    def sql(a: Adapter) = "(" + partials.map { p => p.sql(a) }.mkString(" AND ") + ")"
    val attributes = partials.flatMap { _.attributes }
    new PartialStatement(attributes)(sql)
  }
}

