package org.mauricioszabo.relational_scala.orm.mapper

import org.mauricioszabo.relational_scala.{Selector, QueryBase}
import org.mauricioszabo.relational_scala.results.Attributes

trait LazyResultSet[A] extends QueryBase[Selector with LazyResultSet[A]]
    with IdentityMap[A]
    with Traversable[A] {

  protected val getConnection: () => java.sql.Connection

  def asSelector = this.asInstanceOf[Selector with LazyResultSet[A]]

  def withSelector(fn: Selector => Selector) = {
    val conn = getConnection
    val mapThisTo: Attributes => A = this.mapTo(_: Attributes)
    val selector = new Selector(fn(asSelector))() with LazyResultSet[A] {
      def mapTo(a: Attributes): A = mapThisTo(a)
      protected val getConnection = conn
    }
    selector.table = table
    selector
  }

  def foreach[U](fn: A => U) =
    asSelector.copy(connection=getConnection()).results.foreach { attributes =>
      fn(mapTo(attributes))
    }
}
