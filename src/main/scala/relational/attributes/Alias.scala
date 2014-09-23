package relational.attributes

import relational._

case class Alias(name: String, attribute: AttributeLike) extends AttributeLike {
  override def as(name: String) = new Alias(name, attribute)

  lazy val partial = for(a <- attribute.partial) yield Escape(a.adapter, name) -> a.params
  override lazy val selectPartial = for(a <- attribute.partial)
    yield a.query + " " + Escape(a.adapter, name) -> a.params
}
