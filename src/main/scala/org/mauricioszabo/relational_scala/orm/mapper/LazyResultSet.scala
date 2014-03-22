package relational.orm.mapper

import relational._
import relational.results.Attributes
import relational.Partial

trait LazyResultSet[A] extends Selector
    with ScopeFinder[LazyResultSet[A]]
    with QueryBase[LazyResultSet[A]]
    with IdentityMap[A]
    with Traversable[A]
    with SelectorCompliance
    with Partial {

  def restrict(otherQuery: Selector): LazyResultSet[A] = query { q =>
    val otherJoin = otherQuery.join
    val selfJoin = join.filterNot { e1 => otherJoin.exists { e2 => e1 equivalentTo e2 } }
    q
      .where(otherQuery.where && where)
      .join(otherJoin ++ selfJoin)
      .having(otherQuery.having && having)
  }

  def asSelector = this.asInstanceOf[Selector with LazyResultSet[A]]

  def withSelector(fn: Selector => Selector) = {
    val thisScopes = scopes
    val thisMapTo: Attributes => A = this.mapTo(_: Attributes)
    val selector = new Selector(fn(asSelector)) with LazyResultSet[A] {
      def mapTo(a: Attributes): A = thisMapTo(a)
      protected val scopes = thisScopes
    }
    selector.table = table
    selector
  }

  def foreach[U](fn: A => U) =
    asSelector.results.foreach { attributes =>
      fn(mapTo(attributes))
    }
}
