package relational.comparissions

import relational.Adapter
import relational.attributes._
import relational.PartialStatement

case class Equality(
  protected val comparission: Equality.Comparission,
  protected val attribute: AttributeLike,
  protected val other: AttributeLike) extends Comparission {

  lazy val partial = for {
    a <- attribute.partial
    o <- other.partial
  } yield a.query + " " + comparission.str + " " + o.query -> (a.params ++ o.params)
}

object Equality {
  trait Comparission { def str: String }
  object Equals extends Comparission { def str = "=" }
  object Diferent extends Comparission { def str = "<>" }
  object Gt extends Comparission { def str = ">" }
  object Lt extends Comparission { def str = "<" }
  object GtE extends Comparission { def str = ">=" }
  object LtE extends Comparission { def str = "<=" }
  object Like extends Comparission { def str = "LIKE" }
  object NotLike extends Comparission { def str = "NOT LIKE" }
}
