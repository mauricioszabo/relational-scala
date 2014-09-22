package relational.functions

import relational.{PartialStatement, Partial, Adapter}
import relational.attributes._

trait Function extends Comparable

trait SqlFunction[A] {
  type PartialTuple = String => Seq[Any]

  type Sql = Adapter => String
  type SeqToPartial = Seq[Partial] => (Sql, Seq[Any])
  type DriverAndFunction = (Symbol, SeqToPartial)

  def define(s: (Symbol, String)*) = ???
  def apply(s: Any*): Comparable = ???

  //var function: Adapter => PartialFunction[Symbol, PartialTuple] = _

  ////def define(stringFunctions: (Symbol, String)*) = {
  ////  val fnForEachDriver = stringFunctions.map { case(driver, string) =>
  ////    val fn = { (params: Seq[Partial]) =>
  ////      val attributes = params.flatMap(_.partial.attributes)

  ////      val sqlString = { adapter: Adapter =>
  ////        val matchList = """\$(\d+)""".r.findAllMatchIn(string)

  ////        matchList.foldLeft(string) { (query, matchElement) =>
  ////          val index = matchElement.group(1).toInt
  ////          val partial = params(index).partial
  ////          query.replaceAll("\\$" + index, partial.sql(adapter))
  ////        }
  ////      }
  ////      (sqlString, attributes)
  ////    }
  ////    (driver, fn)
  ////  }
  ////  defineByFunction(fnForEachDriver: _*)
  ////}

  ////def defineByFunction(functions: DriverAndFunction*) = function = functions.toMap

  //def defineByFunction(functions: Adapter => PartialFunction[Symbol, PartialTuple]) = {
  //  function = functions
  //}

  //def apply(params: Any*): Comparable = {
  //  val normalized = params.map { p => Attribute.wrap(p) }
  //  val (query, attributes) = getFunction(normalized)
  //  createFn(query, attributes, normalized)
  //}

  //private def getFunction: Seq[Partial] => (Sql, Seq[Any]) = {
  //  val fn = function(
  //  function.applyOrElse(
  //  function.get(adapter.currentDriver) match {
  //    case Some(function) => function
  //    case _ => function('all)
  //  }

  protected def createFn(sql: Sql, attributes: Seq[Any], n: Seq[Partial]): Comparable =
    new Function {
      lazy val partial = new PartialStatement(attributes)(sql)
    }
}

abstract class SqlAggregateFunction[A] extends SqlFunction[A] {
  val index: Int

  override protected def createFn(sql: Sql, attrs: Seq[Any], norm: Seq[Partial]): Function with Aggregation =
    new Function with Aggregation {
      lazy val partial = new PartialStatement(attrs)(sql)
      val aggregated = norm(index).asInstanceOf[AttributeLike]
    }
}
