package relational.queries.attr_helpers

import scala.language.dynamics
import scala.language.implicitConversions
import relational.tables.TableLike
import relational.attributes.{Comparable, Attribute => RAttr}
import relational.queries.FullQuery
import relational.joins.{LeftJoin => LJoin, RightJoin, InnerJoin}
import relational.tables.{TableLike, Table => RTable, Alias => TableAlias}

class FakeAttributesFromQuery(query: FullQuery[_, _]) extends AttributesEntry[TableLike] {
  protected val tablesFromJoin = query.selector.join.map(_.table)

  //TODO: Find a better way to pick up names of tables from a list
  protected val tablesMap = (query.selector.from.toVector ++ tablesFromJoin).flatMap { t => t match {
    case RTable(name) => Seq(name -> t)
    case TableAlias(name, _, _) => Seq(name -> t)
    case _ => Seq()
  }}.toMap

  //TODO: Find a better way to pick up names of attributes from a list
  protected val attrMap = query.selector.select.flatMap { a => a match {
    case RAttr(table, name) => Seq(name -> a)
    case _ => Seq()
  }}.toMap

  def selectDynamic(name: String) = tablesMap get name match {
    case Some(table) => table
    case None => throw new IllegalArgumentException("there's no table named "
      + name + " on this SQL")
  }

  def get[A](attributeName: Symbol): A = attrMap get attributeName.name match {
    case Some(attribute) =>
      attributesBeingUsed += FakeAttribute(attribute)
      default[A]
    case None => throw new IllegalArgumentException("there's no attribute named " +
      attributeName + " in this SQL")
  }
}
