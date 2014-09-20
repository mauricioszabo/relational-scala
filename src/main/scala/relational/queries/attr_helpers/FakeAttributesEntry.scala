package relational.queries.attr_helpers

import scala.language.dynamics
import scala.language.implicitConversions
import relational.Adapter
import relational.tables.TableLike
import relational.attributes.{Comparable, Attribute => RAttr, Alias => AttrAlias}
import relational.queries.FullQuery
import relational.joins.{LeftJoin => LJoin, RightJoin, InnerJoin}
import relational.tables.{TableLike, Table => RTable, Alias => TableAlias}

import java.sql.ResultSet

class FakeAttributesEntry(table: TableLike)(implicit a: Adapter) extends AttributesEntry[Comparable] {
  def selectDynamic(attributeName: String): Comparable = table(attributeName)

  def get[A](attributeName: Symbol): A = {
    addAttributeToList(FakeAttribute(table(attributeName.name)))
    default[A]
  }

  def get[A](function: relational.functions.SqlFunction[A],params: relational.Partial*): A = {
    val fn = function(params: _*)
    val regexp = """[^\d\w]+""".r
    val aliasName = regexp.replaceAllIn(fn.partial.toPseudoSQL, "_").toLowerCase
    addAttributeToList(FakeAttribute(fn.as(aliasName)))
    default[A]
  }

  def get[A](function: relational.functions.SqlFunction[A], attributeName: Symbol): A = {
    val attr = table(attributeName)
    val fn = function(attr)
    val regexp = """[^\d\w]+""".r
    val aliasName = regexp.replaceAllIn(fn.partial.toPseudoSQL, "_").toLowerCase
    addAttributeToList(FakeAttribute(AttrAlias(aliasName, fn)(a)))
    default[A]
  }

  protected def addAttributeToList(attr: FakeAttribute) {
    attributesBeingUsed += attr
  }
}
