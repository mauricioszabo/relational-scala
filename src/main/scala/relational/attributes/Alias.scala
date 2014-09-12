package relational.attributes

import relational._

class Alias(name: String, query: String, attributes: Seq[Any]) extends AttributeLike {
  override def as(name: String) = new Alias(name, query, attributes)
  lazy val partial = new PartialStatement(Escape(name), attributes)
  override lazy val selectPartial = new PartialStatement(query + " " + name, attributes)
}
