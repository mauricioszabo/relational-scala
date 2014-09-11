package relational.attributes

import relational.PartialStatement

class Literal(any: Any) extends Comparable {
  lazy val partial = new PartialStatement("?", List(any))
}
