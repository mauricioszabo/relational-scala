package relational.comparissions

import relational.attributes.AttributeLike
import relational.PartialStatement

case class IsNull(attr: AttributeLike) extends Comparission {
  lazy val partial = for(a <- attr.partial) yield a.query + " IS NULL" -> a.params
}

case class NotNull(attr: AttributeLike) extends Comparission {
  lazy val partial = for(a <- attr.partial) yield a.query + " IS NOT NULL" -> a.params
}
