package relational.queries.attr_helpers

import scala.language.dynamics
import scala.language.implicitConversions
import relational.tables.TableLike
import relational.attributes.{Comparable, Attribute => RAttr}
import relational.queries.FullQuery
import relational.joins.{LeftJoin => LJoin, RightJoin, InnerJoin}
import relational.tables.{TableLike, Table => RTable, Alias => TableAlias}

import java.sql.ResultSet

class FakeAttributesEntry(table: TableLike) extends AttributesEntry[Comparable] {
  def selectDynamic(attributeName: String): Comparable = table(attributeName)

  def get[A](attributeName: Symbol): A = {
    val attribute = FakeAttribute(table(attributeName.name))
    attributesBeingUsed += attribute
    default[A]
  }
}
