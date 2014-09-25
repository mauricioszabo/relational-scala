package relational.functions

import relational.{PartialStatement, Partial, Adapter}
import relational.attributes._

trait Function extends Comparable

trait SqlFunction[A] {
  type Sql = Adapter => String
  var function = {s: Seq[Any] => PartialStatement { a => "" -> Nil }}

  def define(s: (Symbol, String)*) = ???

  def defineByFunction(fns: Seq[Partial] => PartialFunction[Symbol, PartialStatement]) {
    def fn(params: Seq[Any]) = {
      val normalized = params map Attribute.wrap
      val pf: PartialFunction[Symbol, PartialStatement] = fns(normalized)
      PartialStatement { adapter =>
        val partial = pf.lift(adapter.currentDriver).getOrElse(pf('all))
        partial.tuple(adapter)
      }
    }
    function = fn
  }

  def apply(params: Any*): Comparable = new Function {
    lazy val partial = function(params)
  }

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
  //  new Function {
  //    lazy val partial = new PartialStatement(attributes)(sql)
  //  }
}

abstract class SqlAggregateFunction[A] extends SqlFunction[A] {
  val index: Int

  override def apply(params: Any*): Comparable = new Function with Aggregation {
    lazy val partial = function(params)
    lazy val aggregated = Attribute.wrap(params(index)).asInstanceOf[AttributeLike]
  }
}
