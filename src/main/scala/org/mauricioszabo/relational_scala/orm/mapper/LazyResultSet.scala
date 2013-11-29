package org.mauricioszabo.relational_scala.orm.mapper

import org.mauricioszabo.relational_scala._
import org.mauricioszabo.relational_scala.results.Attributes
import org.mauricioszabo.relational_scala.Partial

trait LazyResultSet[A] extends QueryBase[LazyResultSet[A]]
    with IdentityMap[A]
    with Traversable[A]
    with Partial {

  val select: Seq[attributes.AttributeLike]
  val from: Seq[tables.TableLike]
  val where: comparissions.Comparission
  val group: Seq[attributes.AttributeLike]
  val having: comparissions.Comparission
  val join: Seq[joins.Join]
  val order: Seq[Partial]
  val connection: java.sql.Connection
  val limit: Int
  val offset: Int

  protected val getConnection: () => java.sql.Connection

  def asSelector = this.asInstanceOf[Selector with LazyResultSet[A]]

  def withSelector(fn: Selector => Selector) = {
    val conn = getConnection
    val mapThisTo: Attributes => A = this.mapTo(_: Attributes)
    val selector = new Selector(fn(asSelector)) with LazyResultSet[A] {
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
