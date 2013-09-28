package org.mauricioszabo.relational_scala.clauses

import org.mauricioszabo.relational_scala._

class Select(distinct: Boolean, table: tables.TableLike, listOfAttributes: Any*)
    extends Seq[attributes.AttributeLike] with Partial {

  def iterator = attributeList.iterator
  def apply(idx: Int) = attributeList(idx)
  def length = listOfAttributes.length

  lazy val attributeList = convert(listOfAttributes.toList)

  lazy val partial = {
    val nil = Seq[Any]()
    val(queries, attributes) = attributeList.foldLeft(nil -> nil) { case ((query, attributes), attr) =>
      val sp = attr.selectPartial
      ( query :+ sp.query, attributes ++ sp.attributes)
    }

    val clause = if(distinct)
      "SELECT DISTINCT "
    else
      "SELECT "
    new PartialStatement(clause + queries.mkString(", "), attributes)
  }

  private def convert(list: List[Any]): List[attributes.AttributeLike] = list match {
    case Nil => Nil
    case head::tail => head match {
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
}
