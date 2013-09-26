package org.mauricioszabo.relational_scala

import org.mauricioszabo.relational_scala._
import org.mauricioszabo.relational_scala.results._

case class Selector(
    select: Seq[attributes.AttributeLike],
    from: Seq[tables.TableLike],
    where: comparissions.Comparission = null,
    group: Seq[attributes.AttributeLike] = Nil,
    having: comparissions.Comparission = null,
    join: Seq[joins.Join] = Nil,
    order: Seq[Partial] = Nil,
    connection: java.sql.Connection = null
  ) extends Partial {

  def this(s: Selector) = this(s.select, s.from, s.where, s.group, s.having, s.join, s.order, s.connection)

  def results = {
    val statement = partial.createStatement(connection)
    try {
      new Promisse(statement.executeQuery)
    } catch {
      case e: java.sql.SQLException => throw new java.sql.SQLException(
        "Error on SQL string: the offending code was:\n" +
        partial.toPseudoSQL + ";",
        e
      )
    }
  }

  lazy val partial = {
    var tuple = constructSelect
    tuple = multiElementsQuery(" FROM ", from, tuple)
    join.foreach { j => tuple = constructQuery(tuple, " ", j) }

    if(where != null) tuple = whereQuery(" WHERE ", tuple)
    tuple = multiElementsQuery(" GROUP BY ", group, tuple)
    if(having != null) tuple = whereQuery(" HAVING ", tuple)

    if(!order.isEmpty) tuple = constructOrder(tuple)

    val(query, list) = tuple
    new PartialStatement(query, list)
  }

  private def constructSelect = {
    val(query, attributes) = select.foldLeft( (Seq[String](), Seq[Any]()) ) { case ((query, attributes), partial) =>
      val sp = partial.selectPartial
      ( query :+ sp.query, attributes ++ sp.attributes)
    }
    ("SELECT " + query.mkString(", "), attributes)
  }

  private def multiElementsQuery(before: String, elements: Seq[Partial], tuple: (String, Seq[Any])): (String, Seq[Any]) = {
    if(elements.isEmpty) return tuple
    val first::rest = elements
    rest.foldLeft(constructQuery(tuple, before, first)) { constructQuery(_, ",", _) }
  }

  private def whereQuery(before: String, tuple: (String, Seq[Any])) = {
    constructQuery(tuple, before, where)
  }

  private def constructQuery(tuple: (String, Seq[Any]), before: String, element: Partial) = {
    var (query, list) = tuple
    val partial = element.partial
    list ++= partial.attributes
    (query + before + partial.query, list)
  }

  private def constructOrder(tuple: (String, Seq[Any])) = {
    val (tquery, tattributes) = tuple
    val(query, attributes) = order.foldLeft( (Seq[String](), Seq[Any]()) ) { case ((query, attributes), partial) =>
      val sp = partial.partial
      ( query :+ sp.query, attributes ++ sp.attributes)
    }

    val rquery = tquery + " ORDER BY " + query.mkString("), (")
    (rquery, attributes)
  }
}
