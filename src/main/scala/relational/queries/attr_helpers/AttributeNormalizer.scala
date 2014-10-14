package relational.queries.attr_helpers

import relational.queries.Query
import relational.clauses.Select
import relational.queries.Table
import relational.comparissions._
import relational.functions.Aggregation
import relational.attributes.{Attribute => RAttr, AttributeLike, Alias}
import relational.tables.TableLike
import relational.joins._
import relational.Selector
import relational.analyzers.QueryAnalyzer

class AttributeNormalizer[U](oldSelector: Selector, attributes: Seq[AttributeLike],
                              newTables: Vector[TableLike], newConditions: Comparission) {

  var having = oldSelector.having
  val selectAttrs = oldSelector.select ++ attributes
  var from = (newTables ++ oldSelector.from).toList
  var joins = oldSelector.join.toList
  val groups = oldSelector.group.toBuffer ++ extractGroupBy(selectAttrs)
  val where = normalizeComparissions(newConditions && oldSelector.where)

  private def normalizeComparissions(c: Comparission): Comparission = c match {
    case Equality(_, fun: Aggregation, _) => extractHaving(c)
    case Equality(_, _, fun: Aggregation) => extractHaving(c)
    case Equality(_, a1: RAttr, a2: RAttr) => extractJoins(a1.table, a2.table, c.asInstanceOf[Equality])
    case And(comparissions) =>
      comparissions.foldLeft(None: Comparission) { (previous, comp) =>
        previous && normalizeComparissions(comp)
      }
    case _ => c
  }

  private def extractJoins(t1: TableLike, t2: TableLike, comparission: Equality) = {
    val prev = from.toSet
    val currentTable = from.head
    if(t1 != t2 && prev.contains(t1) && prev.contains(t2)) {
      val toRemove = (Set(t1, t2) - currentTable).head
      from = from.filterNot(_ == toRemove)
      val join = new InnerJoin(toRemove, comparission)
      joins = join::joins.toList
      None
    } else {
      comparission
    }
  }

  private def extractHaving(c: Comparission) = {
    having = having && c
    None
  }

  private def extractGroupBy(attributes: Seq[AttributeLike]) = {
    val nonAliased = attributes.map {
      case Alias(_, a: Aggregation) => a.aggregated
      case Alias(_, a) => a
      case a: AttributeLike => a
    }
    val analyzer = new QueryAnalyzer(oldSelector.copy(select=Select.select(nonAliased: _*)))

    val groupingFunctions = attributes.flatMap {
      case(a: Aggregation) => Seq(a.aggregated)
      case Alias(_, a: Aggregation) => Seq(a.aggregated)
      case _ => Seq()
    }.toSet
    val nonGrouped = (nonAliased diff groupingFunctions.toSeq).toSet

    for {
      fields <- analyzer.fields
      if !(fields & groupingFunctions).isEmpty
      ungroupedMatches = (fields & nonGrouped.toSet)
      if !ungroupedMatches.isEmpty
    } yield ungroupedMatches.head
  }

  def selector = oldSelector.copy(
    select=Select.select(selectAttrs: _*),
    from=from,
    join=joins,
    where=where,
    group=groups,
    having=having
  )
}
