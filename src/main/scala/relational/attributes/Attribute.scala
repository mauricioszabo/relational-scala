package relational.attributes

import relational._

case class Attribute(val table: tables.TableLike, val name: String) extends Comparable {
  lazy val partial = new PartialStatement(Nil)(a =>
    table.representation(a) + "." + Escape(a, name))
}

object Attribute {
  def wrap(any: Any): Comparable = any match {
    case a: Attribute => a
    case _ => new attributes.Literal(any)
  }
}
