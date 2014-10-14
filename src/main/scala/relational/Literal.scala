package relational.attributes

import relational.PartialStatement

case class Literal(any: Any) extends Comparable {
  lazy val partial = PartialStatement(a => "?" -> List(any))
}
