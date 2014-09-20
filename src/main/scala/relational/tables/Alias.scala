package relational.tables

import relational._
import relational.attributes.{Attribute, Escape}
import relational._

case class Alias(private val name: String,
                 private val table: Partial)(implicit a: Adapter) extends TableLike {

  def as(name: String) = new Alias(name, table)
  lazy val representation = Escape(name)
  lazy val partial = table match {
    case _: TableLike => new PartialStatement(tPartial.query + " " + Escape(name), tPartial.attributes)
    case _: FullSelect => new PartialStatement("(" + tPartial.query + ") " + Escape(name), tPartial.attributes)
  }

  def apply(attribute: String) = new Attribute(this, attribute)

  private lazy val tPartial = table.partial
}
