package relational.tables

import relational._
import relational.attributes.{Attribute, Escape}
import relational._

case class Alias(private val name: String,
                 private val table: Partial) extends TableLike {

  def as(name: String) = new Alias(name, table)
  def representation(a: Adapter) = Escape(a, name)
  def partial = for( p <- table.partial ) yield table match {
    case _: TableLike => (p.query + " " + Escape(p.adapter, name), p.params)
    case _: FullSelect => ( "(" + p.query + ") " + Escape(p.adapter, name), p.params)
  }

  def apply(attribute: String) = new Attribute(this, attribute)
}
