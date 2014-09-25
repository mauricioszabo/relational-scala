package relational.attributes

import relational.PartialStatement

class Literal(any: Any) extends Comparable {
  lazy val partial = PartialStatement(a => "?" -> List(any))
}
