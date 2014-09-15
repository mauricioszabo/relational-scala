package relational.joins

import relational._

case class InnerJoin(table: tables.TableLike, condition: comparissions.Comparission) extends Join {
  lazy val partial = newPartial(table, condition, "INNER JOIN")
}
