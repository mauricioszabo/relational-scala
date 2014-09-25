package relational.queries.attr_helpers

import relational.attributes.AttributeLike

case class FakeAttribute(attribute: AttributeLike) {
  override def equals(a: Any) = a match {
    case FakeAttribute(another) => attribute equivalentTo another
    case _ => false
  }
}
