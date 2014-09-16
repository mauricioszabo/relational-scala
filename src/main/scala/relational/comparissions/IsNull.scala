package relational.comparissions

import relational.attributes.AttributeLike
import relational.PartialStatement

class IsNull(attr: AttributeLike) extends Comparission {
  lazy val partial = {
    val attrPartial = attr.partial
    new PartialStatement(
      attrPartial.query + " IS NULL",
      attrPartial.attributes
    )
  }
}
