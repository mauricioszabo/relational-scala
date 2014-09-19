package relational.queries

import attr_helpers._
import relational._
import relational.clauses.Select
import relational.tables.{TableLike, Table => RTable, Alias => TableAlias}
import java.sql.ResultSet

class FullQuery[A, U](selector: Selector, resultStructure: AttributesEntry[A] => U)
        extends Query[A, U](selector, resultStructure) {

  def withFilter(fn: FakeAttributesFromQuery => comparissions.Comparission) = {
    val entries = new FakeAttributesFromQuery(this)
    val newSel = selector.copy(where=selector.where && fn(entries))
    new FullQuery[A, U](newSel, resultStructure)
  }

  def map[B](fn: AttributesEntry[FakeAttributesEntry] => B) = {
    val entries = new FakeAttributesFromQuery(this)
    fn(entries)

    val normalizer = new AttributeNormalizer(selector.from.toList, selector.join.toList, selector.where)

    val newSelector = selector.copy(
      select=clauses.Select.select(selector.from.head, entries.attributes :_*),
      from=normalizer.from,
      where=normalizer.where,
      join=normalizer.joins
    )
    new FullQuery[FakeAttributesEntry, B](newSelector, fn)
  }

  def flatMap[A, U](fn: FakeAttributesFromQuery => Query[A, U]) = {
    val entries = new FakeAttributesFromQuery(this)
    val query = fn(entries)
    val newSelector = joinSelectors(entries, query)
    new FullQuery[A, U](newSelector, query.resultStructure)
  }

  private def joinSelectors[U](entries: FakeAttributesFromQuery, query: Query[_, U]) = {
    val currentSelector = query.selector
    val allFields = entries.attributes ++ currentSelector.select
    val allTables = selector.from.toVector ++ currentSelector.from
    val allJoins = currentSelector.join.toList
    val conditions = selector.where && currentSelector.where
    val normalizer = new AttributeNormalizer(allTables.toList, allJoins, conditions)

    selector.copy(
      select=Select.select(selector.from.head, allFields: _*),
      from=normalizer.from,
      where=normalizer.where,
      join=normalizer.joins
    )
  }
}
