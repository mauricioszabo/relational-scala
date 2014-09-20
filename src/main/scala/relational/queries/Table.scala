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

  def as(name: String) = new Table(table.as(name), filter)(adapter)

  def withFilter(fn: tables.TableLike => comparissions.Comparission) = {
    new Table(table, fn(table))
  }

  def map[U](fn: AttributesEntry[Comparable] => U) = {
    val entries = new FakeAttributesEntry(table)
    fn(entries)

    val normalized = new AttributeNormalizer(
      Selector(Select.select(table), List(table)),
      entries.attributes,
      Vector(),
      filter
    )

    new FullQuery[Comparable, U](normalized.selector, fn)
  }

  def flatMap[A, U](fn: FakeAttributesEntry => Query[A, U]) = {
    val entries = new FakeAttributesEntry(table)
    val query = fn(entries)
    val selector = joinSelectors(entries, query)
    new FullQuery[A, U](selector, query.resultStructure)
  }

  private def joinSelectors[U](entries: FakeAttributesEntry, query: Query[_, U]) = {
    val selector = query.selector
    val normalized = new AttributeNormalizer(
      query.selector,
      entries.attributes,
      Vector(table),
      filter
    )
    normalized.selector
  }
}

object Table {
  def apply(name: String)(implicit adapter: Adapter) = new Table(new tables.Table(name))
  def apply(name: Symbol)(implicit adapter: Adapter) = new Table(new tables.Table(name.name))
}
