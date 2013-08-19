package org.mauricioszabo.relational_scala.joins

import org.mauricioszabo.relational_scala._

class InnerJoin(table: tables.TableLike, condition: comparissions.Comparission) extends Join {
  lazy val partial = newPartial(table, condition, "INNER JOIN")
}
