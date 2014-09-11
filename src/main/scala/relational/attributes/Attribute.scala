package relational.attributes

import relational._

class Attribute(val table: tables.TableLike, protected val name: String) extends Comparable {
  lazy val partial = new PartialStatement(table.representation + "." + name, Nil)
}

object Attribute {
  def wrap(any: Any): Comparable = any match {
    case a: Attribute => a
    case _ => new attributes.Literal(any)
  }
}
