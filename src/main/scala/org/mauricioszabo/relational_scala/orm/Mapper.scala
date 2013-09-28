package org.mauricioszabo.relational_scala.orm

import org.mauricioszabo.relational_scala._
import scala.reflect.ClassTag
import java.lang.reflect.Field
import org.mauricioszabo.relational_scala.results.Attributes
import org.mauricioszabo.relational_scala.orm.mapper.LazyResultSet
import org.mauricioszabo.relational_scala.clauses.Select

class Mapper[A <: Mapping](implicit val tag: ClassTag[A])
    extends QueryBase[LazyResultSet[A]]
    with mapper.IdentityMap[A]
    with Traversable[A] {

  protected var pk = 'id
  protected var getConnection = config.Connection.getConnection

  private val klass = tag.runtimeClass
  private val mappings = {
    val f = klass.getDeclaredField("mappings")
    f.setAccessible(true)
    f
  }
  private lazy val mod = classOf[Field].getDeclaredField("modifiers")

  def foreach[U](fn: A => U) = toLazyResultSet.foreach(fn)

  protected[relational_scala] def withSelector(s: Selector => Selector) = toLazyResultSet.withSelector(s)

  private def toLazyResultSet = {
    val c = this.getConnection
    val mapThisTo = this.mapTo(_: Attributes)
    val thisTable = this.table

    new Selector(
                  select=new Select(false, table, new attributes.All()),
                  from=List(table)
                ) with LazyResultSet[A] {
      protected val getConnection = c
      def mapTo(a: Attributes): A = mapThisTo(a)

      this.table = thisTable
    }
  }

  def mapTo(attributes: Attributes): A = {
    val instance = klass.newInstance
    val map = attributes.attribute.map { case(k, v) => (k, v.obj) }
    mappings.set(instance, map)
    instance.asInstanceOf[A]
  }

  //def find(id: Any) = where { table(pk) -> id }.head
}
