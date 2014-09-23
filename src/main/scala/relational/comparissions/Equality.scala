package relational.comparissions

import relational.Adapter
import relational.attributes._
import relational.PartialStatement

case class Equality(
  protected val comparission: String,
  protected val attribute: AttributeLike,
  protected val other: AttributeLike) extends Comparission {

  lazy val partial = for {
    a <- attribute.partial
    o <- other.partial
  } yield a.query + " " + comparission + " " + o.query -> (a.params ++ o.params)
}
