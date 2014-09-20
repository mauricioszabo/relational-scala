package relational.attributes

import relational._

case class Alias(name: String, attribute: AttributeLike)(implicit a: Adapter) extends AttributeLike {
  override def as(name: String) = new Alias(name, attribute)

  lazy val partial = {
    new PartialStatement(Escape(name), attrPartial.attributes)
  }
  override lazy val selectPartial = new PartialStatement(
    attrPartial.query + " " + Escape(name), attrPartial.attributes)

  private lazy val attrPartial = attribute.partial
}
