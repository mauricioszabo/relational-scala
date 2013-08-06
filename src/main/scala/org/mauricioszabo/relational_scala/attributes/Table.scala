package org.mauricioszabo.relational_scala.attributes

import org.mauricioszabo.relational_scala.comparissions._
import org.mauricioszabo.relational_scala.PartialStatement
import org.mauricioszabo.relational_scala.tables.TableLike

class Table(table: TableLike, name: String) extends Attribute {
  def representation = table.representation + "." + name
}
