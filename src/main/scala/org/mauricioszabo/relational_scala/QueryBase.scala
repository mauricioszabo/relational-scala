package org.mauricioszabo.relational_scala
import org.mauricioszabo.relational_scala.comparissions.Comparission
import org.mauricioszabo.relational_scala.clauses.Select

trait QueryBase[A] {
  private var relationalTable: tables.TableLike = {
    val regex = """.*\$([^\$]+)\$.*""".r
    val name = regex.replaceFirstIn(getClass.getName, "$1").toLowerCase
    new tables.Table(name)
  }

  def table: tables.TableLike = this match {
    case s: Selector with QueryBase[A] => s.from.head
    case _ => relationalTable
  }

  protected def table_=(tableName: String) { relationalTable = new tables.Table(tableName) }
  protected def table_=(table: tables.TableLike) { relationalTable = table }

  def query(query: Function1[Symbol, attributes.Attribute] with QueryBase[A] => A) = {
    val thisSelector = this.withSelector(_)
    val tableQuery = new (Symbol => attributes.Attribute) with QueryBase[A] {
      def apply(s: Symbol) = table(s)
      def withSelector(s: Selector => Selector): A = thisSelector(s)
    }
    tableQuery.table = table
    query(tableQuery)
  }

  def select(attributes: Any*) = withSelector { s =>
    s.copy(select=Select.select(table, attributes: _*))
  }

  def distinct(attributes: Any*) = withSelector { s =>
    val converted = convert(attributes.toList)
    s.copy(select=Select.distinct(table, attributes: _*))
  }

  def group(attributes: Any*) = withSelector { s =>
    val converted = convert(attributes.toList)
    s.copy(group=converted)
  }

  private def convert(list: List[Any]): List[attributes.AttributeLike] = list match {
    case Nil => Nil
    case head::tail => head match {
      case s: Symbol => table(s)::convert(tail)
      case s: String => table(s)::convert(tail)
      case a: attributes.AttributeLike => a::convert(tail)
    }
  }

  def from(tables: Any*) = withSelector { s =>
    val converted = convertTable(tables.toList)
    s.copy(from=converted)
  }

  private def convertTable(list: List[Any]): List[tables.TableLike] = list match {
    case Nil => Nil
    case head::tail => head match {
      case s: Symbol => new tables.Table(s.name)::convertTable(tail)
      case s: String => new tables.Table(s)::convertTable(tail)
      case a: tables.TableLike => a::convertTable(tail)
    }
  }

  def where(comp: Comparission) = withSelector { s => s.copy(where=comp) }
  def where(query: (Symbol => attributes.Attribute) => Comparission) = withSelector { selector =>
    val symbol2table = { s: Symbol => selector.from.head(s) }
    selector.copy(where=query(symbol2table))
  }

  def join(table: tables.TableLike) = new joins.JoinHelper(this, table, 'inner)
  def join(table: Symbol): joins.JoinHelper[A] = join(new tables.Table(table.name))

  def leftJoin(table: tables.TableLike) = new joins.JoinHelper(this, table, 'left)
  def leftJoin(table: Symbol): joins.JoinHelper[A] = leftJoin(new tables.Table(table.name))

  def rightJoin(table: tables.TableLike) = new joins.JoinHelper(this, table, 'right)
  def rightJoin(table: Symbol): joins.JoinHelper[A] = rightJoin(new tables.Table(table.name))

  def order(orders: Partial*) = withSelector { s => s.copy(order=orders) }
  def order(orders: tables.TableLike => Seq[Partial]) = withSelector { s =>
    s.copy(order=orders(table))
  }

  protected[relational_scala] def withSelector(fn: Selector => Selector): A
}
