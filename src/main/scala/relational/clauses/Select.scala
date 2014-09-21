package relational.clauses

import relational._

class Select(distinct: Boolean, table: tables.TableLike, listOfAttributes: Any*)
    extends Seq[attributes.AttributeLike] with Partial {

  def iterator = attributeList.iterator
  def apply(idx: Int) = attributeList(idx)
  def length = listOfAttributes.length
  val isDistinct = distinct

  lazy val attributeList = convert(listOfAttributes.toList).toVector

  lazy val partial = {
    val sqlNil = Seq[Adapter => String]()
    val nil = Seq[Any]()
    val(sqls, attributes) = attributeList.foldLeft(sqlNil -> nil) { case ((sql, attributes), attr) =>
      val sp = attr.selectPartial
      (sql :+ sp.sql, attributes ++ sp.attributes)
    }

    val clause = if(distinct)
      "SELECT DISTINCT "
    else
      "SELECT "
    new PartialStatement(attributes)(a => clause + sqls.map(s => s(a)).mkString(", "))
  }

  private def convert(list: List[Any]): List[attributes.AttributeLike] = list match {
    case Nil => Nil
    case head::tail => head match {
      case '* => (table.*)::convert(tail)
      case "*" => (table.*)::convert(tail)
      case s: Symbol => table(s)::convert(tail)
      case s: String => table(s)::convert(tail)
      case a: attributes.AttributeLike => a::convert(tail)
    }
  }
}

object Select {
  def select(table: tables.TableLike, attrs: Any*) =
    new Select(false, table, attrs: _*)

  def distinct(table: tables.TableLike, attrs: Any*) =
    new Select(true, table, attrs: _*)

  def select(attrs: attributes.AttributeLike*) = new Select(false, null, attrs: _*)
}
