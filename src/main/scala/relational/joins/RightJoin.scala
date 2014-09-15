package relational.joins

import relational._

case class RightJoin(table: tables.TableLike, condition: comparissions.Comparission) extends Join {
  lazy val partial = newPartial(table, condition, "RIGHT JOIN")
}
