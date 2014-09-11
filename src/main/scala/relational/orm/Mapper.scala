package relational.orm

import relational._
import scala.reflect.ClassTag
import java.lang.reflect.Field
import relational.results.Attributes
import relational.orm.mapper.LazyResultSet
import relational.clauses.Select

class Mapper[A](implicit val tag: ClassTag[A])
    extends QueryBase[LazyResultSet[A]]
    with mapper.ScopeDefinition[LazyResultSet[A]]
    with mapper.ScopeFinder[LazyResultSet[A]]
    with mapper.IdentityMap[A]
    with mapper.SelectorCompliance
    with Traversable[A] {

  val select: clauses.Select = new Select(false, table, new attributes.All())
  lazy val from: Seq[tables.TableLike] = List(table)
  val where: comparissions.Comparission = comparissions.None
  val group: Seq[attributes.AttributeLike] = Nil
  val having: comparissions.Comparission = comparissions.None
  val join: Seq[joins.Join] = Nil
  val order: Seq[Partial] = Nil
  lazy val connection: java.sql.Connection = getConnection()
  val limit: Int = -1
  val offset: Int = -1

  protected lazy val scopes = definedScopes
  protected var pk = 'id
  protected var getConnection = config.Connection.getConnection

  private lazy val klass = tag.runtimeClass
  private lazy val mappings = {
    val f = klass.getDeclaredField("mappings")
    f.setAccessible(true)
    f
  }
  private lazy val thisQuery = {
    val f = klass.getDeclaredField("query")
    f.setAccessible(true)
    f
  }
  private lazy val mod = classOf[Field].getDeclaredField("modifiers")

  def foreach[U](fn: A => U) = toLazyResultSet.foreach(fn)

  protected def withSelector(s: Selector => Selector) = toLazyResultSet.withSelector(s)

  private def toLazyResultSet = {
    val thisMapTo = this.mapTo(_: Attributes)
    val thisTable = this.table
    val thisScopes = scopes

    new Selector(select, from, connection=getConnection()) with LazyResultSet[A] {
      def mapTo(a: Attributes): A = thisMapTo(a)
      this.table = thisTable
      protected val scopes = thisScopes
    }
  }

  def mapTo(attributes: Attributes): A = {
    val instance = klass.newInstance
    val map = attributes.attribute.map { case(k, v) => (k, v.obj) }
    mappings.set(instance, map)
    thisQuery.set(instance, map)
    instance.asInstanceOf[A]
  }

  def find(id: Int) = where { table(pk) -> id }.head
  def find(id: String) = where { table(pk) -> id }.head
  //If you have a table with PK other than String or Int, I've got baad news for you...
}
