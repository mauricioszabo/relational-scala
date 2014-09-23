package relational.comparissions

import relational.Adapter
import relational.attributes._
import relational.PartialStatement

case class In(attribute: AttributeLike, list: Seq[Any]) extends Comparission {
  lazy val partial = for(a <- attribute.partial)
  yield a.query + " IN (" + list.map(_=>"?").mkString(",") + ")" -> (a.params ++ list)
}
