package org.mauricioszabo.relational_scala.attributes

import org.mauricioszabo.relational_scala._

class Attribute(val table: tables.TableLike, name: String) extends Comparable {
  def partial = new PartialStatement(table.representation + "." + name, Nil)
}
