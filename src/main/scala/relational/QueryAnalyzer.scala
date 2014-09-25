package relational

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
    case Equality("=", one, two) =>
      val subtracted = current.filterNot { s => s.contains(one) || s.contains(two) }
      val combined = (current -- subtracted).flatten
      subtracted + combined
    case And(list) =>
      list.foldLeft(current) { (sets, e) =>
        extractConditions(e, sets)
      }
    case _ => current
  }


  private def allConditions: Comparission = {
    val seed = selector.where && selector.having
    selector.join.foldLeft(seed) {
      case(comp, Join(_, c, 'inner)) => comp && c
      case (comp, _) => comp
    }
  }
}
