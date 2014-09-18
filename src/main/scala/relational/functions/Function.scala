package relational.functions

import relational.{PartialStatement, Partial, Adapter}
import relational.attributes._

trait Function extends Comparable

abstract class SqlFunction(implicit adapter: Adapter) {
  type SeqToPartial = Seq[Partial] => (String, Seq[Any])
  type DriverAndFunction = (Symbol, SeqToPartial)

  var function = Map[Symbol, SeqToPartial]()

  def define(stringFunctions: (Symbol, String)*) = {
    val functions = stringFunctions.map { case(driver, string) =>
      val function = { (params: Seq[Partial]) =>
        val matchList = """\$(\d+)""".r.findAllMatchIn(string)

        matchList.foldLeft(string -> List[Any]()) { case((query, attributes), matchElement) =>
          val index = matchElement.group(1).toInt
          val partial = params(index).partial

          val q = query.replaceAll("\\$" + index, partial.query)
          val a = attributes ++ partial.attributes
          (q, a)
        }
      }
      (driver, function)
    }
    defineByFunction(functions: _*)
  }

  def defineByFunction(functions: DriverAndFunction*) = function = functions.toMap

  def apply(params: Any*): Comparable = {
    val normalized = params.map { p => Attribute.wrap(p) }
    val (query, attributes) = getFunction(normalized)
    createFn(query, attributes, normalized)
  }

  private def getFunction: Seq[Partial] => (String, Seq[Any]) =
    function.get(adapter.currentDriver) match {
      case Some(function) => function
      case _ => function('all)
    }

  protected def createFn(query: String, attributes: Seq[Any], n: Seq[Partial]): Comparable =
    new Function {
      lazy val partial = new PartialStatement(query, attributes)
    }
}

abstract class SqlAggregateFunction(implicit adapter: Adapter) extends SqlFunction()(adapter) {
  val index: Int

  override protected def createFn(query: String, attrs: Seq[Any], norm: Seq[Partial]): Function with Aggregation =
    new Function with Aggregation {
      lazy val partial = new PartialStatement(query, attrs)
      val aggregated = norm(index).asInstanceOf[AttributeLike]
    }
}
