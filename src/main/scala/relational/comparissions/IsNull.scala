package relational.comparissions

import relational.attributes.AttributeLike
import relational.PartialStatement

class IsNull(attr: AttributeLike) extends Comparission {
  lazy val partial = {
    val attrPartial = attr.partial
    new PartialStatement(attrPartial.attributes)(a => attrPartial.sql(a) + " IS NULL")
  }
}
