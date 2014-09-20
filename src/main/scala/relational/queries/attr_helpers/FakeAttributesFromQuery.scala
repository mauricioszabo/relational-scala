package relational.queries.attr_helpers

import scala.language.dynamics
import scala.language.implicitConversions
import relational.Adapter
import relational.tables.TableLike
import relational.attributes.{Comparable, Attribute => RAttr, Alias => AttrAlias}
import relational.queries.FullQuery
import relational.joins.{LeftJoin => LJoin, RightJoin, InnerJoin}
import relational.tables.{TableLike, Table => RTable, Alias => TableAlias}

class FakeAttributesFromQuery(query: FullQuery[_, _])
                             (implicit a: Adapter) extends AttributesEntry[FakeAttributesEntry] {
  protected val tablesFromJoin = query.selector.join.map(_.table)

  //TODO: Find a better way to pick up names of tables from a list
  protected val tablesMap = (query.selector.from.toVector ++ tablesFromJoin).flatMap { t => t match {
    case RTable(name) => Seq(name -> t)
    case TableAlias(name, _) => Seq(name -> t)
    case _ => Seq()
  }}.toMap

  //TODO: Find a better way to pick up names of attributes from a list
  protected val attrMap = query.selector.select.flatMap { a => a match {
    case RAttr(table, name) => Seq(name -> a)
    case _ => Seq()
  }}.toMap

  def selectDynamic(name: String) = tablesMap get name match {
    case Some(table) =>
      val self = this
      new FakeAttributesEntry(table) {
        override protected def addAttributeToList(attr: FakeAttribute) {
          self.attributesBeingUsed += attr
        }
      }
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

  def get[A](function: relational.functions.SqlFunction[A],params: relational.Partial*): A = {
    val fn = function(params: _*)
    val regexp = """[^\d\w]+""".r
    val aliasName = regexp.replaceAllIn(fn.partial.toPseudoSQL, "_").toLowerCase
    val attribute = FakeAttribute(AttrAlias(aliasName, fn)(a))
    attributesBeingUsed += attribute
    default[A]
  }

  def get[A](function: relational.functions.SqlFunction[A],param: Symbol): A =
    throw new IllegalArgumentException("Using a symbol is only permitted when we know what table is being refered - " +
                                       "try using 'object.table.get(...)' in place of 'object.get(...)'")
}
