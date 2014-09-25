package relational.queries.attr_helpers

import scala.language.dynamics
import scala.language.implicitConversions
import relational.tables.TableLike
import relational.attributes.{Comparable, Attribute => RAttr}
import relational.queries.FullQuery
import relational.joins.{LeftJoin => LJoin, RightJoin, InnerJoin}
import relational.tables.{TableLike, Table => RTable, Alias => TableAlias}
import relational.functions.SqlFunction
import relational.Partial

import java.sql.ResultSet

trait AttributesEntry[+A] extends Dynamic {
  protected[queries] var attributesBeingUsed = Set[FakeAttribute]()
  protected[queries] def attributes = attributesBeingUsed.map(_.attribute).toVector

  class DefaultValue[A] { var default: A = _ }
  protected def default[B] = new DefaultValue[B]().default

  def selectDynamic(attributeName: String): A

  def maybe[A: Manifest](attributeName: Symbol): Option[A] = any(attributeName) match {
    case attr: A => Some(attr)
    case _ => None
  }

  def any(attributeName: Symbol): Any = get[Any](attributeName)

  def get[A](attributeName: Symbol): A
  def get[A](function: SqlFunction[A], params: Partial*): A
  def get[A](function: SqlFunction[A], param: Symbol): A
}

class AttributesFromResultSet(rs: ResultSet) extends AttributesEntry[Nothing] {
  def selectDynamic(attributeName: String) =
    throw new IllegalArgumentException("getting an 'relational attribute' when constructing " +
      "the result is meaningless - SQL information is already discarded")

  def get[A](attributeName: Symbol): A = {
    rs.getObject(attributeName.name).asInstanceOf[A]
  }

  def get[A](function: relational.functions.SqlFunction[A],param: Symbol): A = ???
  def get[A](function: relational.functions.SqlFunction[A],params: relational.Partial*): A = ???
}
