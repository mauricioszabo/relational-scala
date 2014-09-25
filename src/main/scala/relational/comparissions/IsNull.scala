package relational.comparissions

import relational.attributes.AttributeLike
import relational.PartialStatement

class IsNull(attr: AttributeLike) extends Comparission {
  lazy val partial = for(a <- attr.partial) yield a.query + " IS NULL" -> a.params
}
