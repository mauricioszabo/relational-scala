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
  ) {

  //def this(s: Selector) = this(s.select, s.from, s.where, s.group, s.having, s.join, s.order, s.connection)

  def this(s: Selector)(
    select: Seq[attributes.AttributeLike] = null,
    from: Seq[tables.TableLike] = null,
    where: comparissions.Comparission = null,
    group: Seq[attributes.AttributeLike] = null,
    having: comparissions.Comparission = null,
    join: Seq[joins.Join] = null,
    order: Seq[Partial] = null,
    connection: java.sql.Connection = null
  ) = this(
    if(select     == null) s.select     else select,
    if(from       == null) s.from       else from,
    if(where      == null) s.where      else where,
    if(group      == null) s.group      else group,
    if(having     == null) s.having     else having,
    if(join       == null) s.join       else join,
    if(order      == null) s.order      else order,
    if(connection == null) s.connection else connection
  )

  //def copy(
  //  select: Seq[attributes.AttributeLike] = select,
  //  from: Seq[tables.TableLike] = from,
  //  where: comparissions.Comparission = where,
  //  group: Seq[attributes.AttributeLike] = group,
  //  having: comparissions.Comparission = having,
  //  join: Seq[joins.Join] = join,
  //  order: Seq[Partial] = order,
  //  connection: java.sql.Connection = connection
  //) = new Selector( select, from, where, group, having, join, order, connection) with Query

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
