package relational.attributes

import relational.PartialStatement
import relational._

class AllInTable(table: tables.TableLike) extends AttributeLike {
  lazy val partial = PartialStatement { a =>
    table.representation(a) + ".*" -> Nil
  }
}

