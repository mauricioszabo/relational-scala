package relational.attributes

import relational._

case class Alias(name: String, attribute: AttributeLike) extends AttributeLike {
  override def as(name: String) = new Alias(name, attribute)

  lazy val partial = {
    new PartialStatement(attrPartial.attributes)(a => Escape(a, name) )
  }
  override lazy val selectPartial = new PartialStatement(attrPartial.attributes)(a =>
    attrPartial.sql(a) + " " + Escape(a, name))

  private lazy val attrPartial = attribute.partial
}
