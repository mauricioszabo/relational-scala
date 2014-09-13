package relational.comparissions

import relational.attributes._
import relational.PartialStatement

case class Equality(
  protected val comparission: String,
  protected val attribute: AttributeLike,
  protected val other: AttributeLike) extends Comparission {

  lazy val partial = new PartialStatement(query + other.partial.query,
    ap.attributes ++ other.partial.attributes)

  private lazy val query = ap.query + " " + comparission + " "
  private lazy val ap = attribute.partial
}
