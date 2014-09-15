package relational.attributes

import relational._

case class Attribute(val table: tables.TableLike, val name: String)
               (implicit adapter: Adapter) extends Comparable {
  lazy val partial = new PartialStatement(table.representation + "." + Escape(name), Nil)
}

object Attribute {
  def wrap(any: Any): Comparable = any match {
    case a: Attribute => a
    case _ => new attributes.Literal(any)
  }
}
