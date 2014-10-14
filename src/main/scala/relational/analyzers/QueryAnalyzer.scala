package relational.analyzers

import relational._
import relational.attributes.AttributeLike
import relational.comparissions.{Comparission, None => NullComp, And, Equality}
import relational.joins.Join

class QueryAnalyzer(selector: Selector) {
  lazy val fields = {
    val grouped = selector.select.grouped(1).map(_.toSet).toSet
    val cond = allConditions
    extractConditions(cond, grouped)
  }

  private def extractConditions(comparission: Comparission, current: Set[Set[AttributeLike]]): Set[Set[AttributeLike]] =
  comparission match {
    case Equality(Equality.Equals, one, two) =>
      val subtracted = current.filterNot { s => s.contains(one) || s.contains(two) }
      val combined = (current -- subtracted).flatten
      subtracted + combined
    case And(list) =>
      list.foldLeft(current) { (sets, e) =>
        extractConditions(e, sets)
      }
    case _ => current
  }


  // This brings all conditions that restrict this query, like conditions in joins, in where, and having.
  def allConditions: Comparission = {
    val seed = selector.where && selector.having
    selector.join.foldLeft(seed) {
      case(comp, Join(_, c, 'inner)) => comp && c
      case (comp, _) => comp
    }
  }
}
