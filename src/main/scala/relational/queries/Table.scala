package relational.queries

import attr_helpers._
import scala.language.dynamics
import relational._
import relational.attributes.{AttributeLike, Comparable}
import relational.comparissions.{Comparission, None}
import relational.clauses.Select

class Table protected(val table: tables.TableLike, val filter: Comparission = None)
                     (implicit adapter: Adapter) extends Partial {
  lazy val partial = table.partial

  def withFilter(fn: tables.TableLike => comparissions.Comparission) = {
    new Table(table, fn(table))
  }

  def map[U](fn: AttributesEntry[Comparable] => U) = {
    val entries = new FakeAttributesEntry(table)
    fn(entries)

    val selector = Selector(
      select=Select.select(table, entries.attributes: _*),
      from=List(table),
      where=filter
    )
    new FullQuery[Comparable, U](selector, fn)
  }

  def flatMap[A, U](fn: FakeAttributesEntry => Query[A, U]) = {
    val entries = new FakeAttributesEntry(table)
    val query = fn(entries)
    val selector = joinSelectors(entries, query)
    new FullQuery[A, U](selector, query.resultStructure)
  }

  private def joinSelectors[U](entries: FakeAttributesEntry, query: Query[_, U]) = {
    val selector = query.selector
    val allFields = entries.attributes ++ selector.select
    val allTables = table::selector.from.toList
    val allJoins = selector.join.toList
    val conditions = filter && selector.where
    val normalizer = new AttributeNormalizer(allTables, allJoins, conditions)

    Selector(
      select=Select.select(table, allFields: _*),
      from=normalizer.from,
      where=normalizer.where,
      join=normalizer.joins
    )
  }
}

object Table {
  def apply(name: String)(implicit adapter: Adapter) = new Table(new tables.Table(name))
  def apply(name: Symbol)(implicit adapter: Adapter) = new Table(new tables.Table(name.name))
}
