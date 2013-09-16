package org.mauricioszabo.relational_scala.orm

import org.mauricioszabo.relational_scala._
import scala.reflect.ClassTag
import scala.reflect.runtime.universe._
import java.lang.reflect.Field

class Finder[A <: Mapping](implicit val tag: ClassTag[A]) extends Query with Traversable[A] {
  //private var relationalTable: tables.TableLike = {
  //  val regex = """.*\$([^\$]+)\$.*""".r
  //  val name = regex.replaceFirstIn(getClass.getName, "$1").toLowerCase
  //  new tables.Table(name)
  //}

  //def table = relationalTable
  //protected def table_=(tableName: String) { relationalTable = new tables.Table(tableName) }

  protected var pk = 'id
  protected var getConnection = config.Connection.getConnection

  private val klass = tag.runtimeClass
  private val mappings = {
    val f = klass.getDeclaredField("mappings")
    f.setAccessible(true)
    f
  }
  private lazy val mod = classOf[Field].getDeclaredField("modifiers")

  //override def all: Selector with Finder[A] = this match {
  //  case s: Selector with Query => s
  //  case _ => new Selector(select=List(table.*), from=List(table)) with Finder[A]
  //}

  def foreach[U](fn: A => U) = {
    val connection = getConnection()

    all.copy(connection=connection).results.foreach { attributes =>
      val instance = klass.newInstance
      val map = attributes.attribute.map { case(k, v) => (k, v.obj) }
      mappings.set(instance, map.toSeq)
      fn(instance.asInstanceOf[A])
    }
  }

  //def find(id: Any) = where { table(pk) -> id }.head
}
