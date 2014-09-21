package relational.comparissions

import relational.PartialStatement

case class Not(comparission: Comparission) extends Comparission {
  lazy val partial = new PartialStatement(statement.attributes)(a =>
    "NOT(" + statement.sql(a) + ")")

  private lazy val statement = comparission.partial
}
