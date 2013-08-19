package org.mauricioszabo.relational_scala

import org.mauricioszabo.relational_scala.comparissions.Comparission
import scala.language.implicitConversions

trait Query {
  private var relationalTable: tables.TableLike = {
    val regex = """.*\$([^\$]+)\$.*""".r
    val name = regex.replaceFirstIn(getClass.getName, "$1")
    new tables.Table(name)
  }

  def table = relationalTable
  protected def table_=(tableName: String) { relationalTable = new tables.Table(tableName) }
  protected def table_=(table: tables.TableLike) { relationalTable = table }

  def all: Selector with Query = this match {
    case s: Selector with Query => s
    case _ => new Selector(select=List(table.*), from=List(table)) with Query
  }

  def query(query: Function1[Symbol, attributes.Attribute] with Query => Selector with Query) = {
    val tableQuery = new (Symbol => attributes.Attribute) with Query {
      def apply(s: Symbol) = table(s)
    }
    tableQuery.table = table
    query(tableQuery)
  }

  def select(attributes: Any*) = newSelector {
    val converted = convert(attributes.toList)
    all.copy(select=converted)
  }

  def group(attributes: Any*) = newSelector {
    val converted = convert(attributes.toList)
    all.copy(group=converted)
  }

  private def convert(list: List[Any]): List[attributes.AttributeLike] = list match {
    case Nil => Nil
    case head::tail => head match {
      case s: Symbol => table(s)::convert(tail)
      case s: String => table(s)::convert(tail)
      case a: attributes.AttributeLike => a::convert(tail)
    }
  }

  def from(tables: Any*) = newSelector {
    val converted = convertTable(tables.toList)
    all.copy(from=converted)
  }

  private def convertTable(list: List[Any]): List[tables.TableLike] = list match {
    case Nil => Nil
    case head::tail => head match {
      case s: Symbol => new tables.Table(s.name)::convertTable(tail)
      case s: String => new tables.Table(s)::convertTable(tail)
      case a: tables.TableLike => a::convertTable(tail)
    }
  }

  def where(comp: Comparission) = newSelector { all.copy(where=comp) }
  def where(query: (Symbol => attributes.Attribute) => Comparission) = {
    implicit val symbol2table = { s: Symbol => table(s) }
    newSelector { all.copy(where=query(symbol2table)) }
  }

  def join(table: tables.TableLike) = new joins.JoinHelper(this, table, 'inner)
  def join(table: Symbol): joins.JoinHelper = join(new tables.Table(table.name))

  def leftJoin(table: tables.TableLike) = new joins.JoinHelper(this, table, 'left)
  def leftJoin(table: Symbol): joins.JoinHelper = leftJoin(new tables.Table(table.name))

  def rightJoin(table: tables.TableLike) = new joins.JoinHelper(this, table, 'right)
  def rightJoin(table: Symbol): joins.JoinHelper = rightJoin(new tables.Table(table.name))

  def order(orders: Partial*) = {
    newSelector { all.copy(order=orders) }
  }

  protected[relational_scala] def newSelector(fn: => Selector with Query) = {
    val selector = fn
    selector.table = table
    selector
  }
}
