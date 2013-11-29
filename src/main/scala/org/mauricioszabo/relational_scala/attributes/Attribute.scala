package org.mauricioszabo.relational_scala.attributes

import org.mauricioszabo.relational_scala._

class Attribute(val table: tables.TableLike, protected val name: String) extends Comparable {
  lazy val partial = new PartialStatement(table.representation + "." + name, Nil)
}
