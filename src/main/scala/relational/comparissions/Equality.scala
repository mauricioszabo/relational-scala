package relational.comparissions

import relational.Adapter
import relational.attributes._
import relational.PartialStatement

case class Equality(
  protected val comparission: String,
  protected val attribute: AttributeLike,
  protected val other: AttributeLike) extends Comparission {

  lazy val partial = new PartialStatement(ap.attributes ++ other.partial.attributes)(a =>
    aSql(a) + other.partial.sql(a)
  )

  private def aSql(a: Adapter) = ap.sql(a) + " " + comparission + " "
  private lazy val ap = attribute.partial
}
