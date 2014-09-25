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

    val normalizer = new AttributeNormalizer(
      selector.copy(select=Select.select()),
      entries.attributes,
      Vector(),
      comparissions.None
    )
    new FullQuery[FakeAttributesEntry, B](normalizer.selector, fn)
  }

  def flatMap[A, U](fn: FakeAttributesFromQuery => Query[A, U]) = {
    val entries = new FakeAttributesFromQuery(this)
    val query = fn(entries)
    val newSelector = joinSelectors(entries, query)
    new FullQuery[A, U](newSelector, query.resultStructure)
  }

  private def joinSelectors[U](entries: FakeAttributesFromQuery, query: Query[_, U]) = {
    val normalized = new AttributeNormalizer(
      query.selector,
      entries.attributes,
      selector.from.toVector,
      selector.where
    )

    val newSelector = normalized.selector
    newSelector.copy(
      join=selector.join ++ newSelector.join,
      having=selector.having && newSelector.having
    )
  }
}
