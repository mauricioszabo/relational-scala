package relational.queries.attr_helpers

import relational.attributes.AttributeLike

trait Attribute {
}

case class FakeAttribute(attribute: AttributeLike) extends Attribute {
  override def equals(a: Any) = a match {
    case FakeAttribute(another) => attribute equivalentTo another
    case _ => false
  }
}
