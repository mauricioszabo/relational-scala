package relational.tables

import relational._
import relational.attributes.{Attribute, Escape}
import relational._

case class Alias(private val name: String,
                 private val table: Partial) extends TableLike {

  def as(name: String) = new Alias(name, table)
  lazy val representation = Escape(name)
  def partial = {
    val ps = new PartialStatement(tPartial.attributes)(_: Adapter => String)
    table match {
      case _: TableLike => ps { a => tPartial.sql(a) + " " + Escape(a, name) }
      case _: FullSelect => ps { a => "(" + tPartial.sql(a) + ") " + Escape(a, name) }
    }
  }

  def apply(attribute: String) = new Attribute(this, attribute)

  private lazy val tPartial = table.partial
}
