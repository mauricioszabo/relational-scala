package relational.attributes

import relational._

case class Attribute(val table: tables.TableLike, val name: String) extends Comparable {
  lazy val partial = PartialStatement { a =>
    table.representation(a) + "." + Escape(a, name) -> Nil
  }
}

object Attribute {
  def wrap(any: Any): AttributeLike = any match {
    case a: AttributeLike => a
    case _ => new attributes.Literal(any)
  }
}
