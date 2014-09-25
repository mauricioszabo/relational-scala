package relational.comparissions

import relational.PartialStatement

case class Not(comparission: Comparission) extends Comparission {
  lazy val partial = for(c <- comparission.partial) yield "NOT(" + c.query + ")" -> c.params
}
