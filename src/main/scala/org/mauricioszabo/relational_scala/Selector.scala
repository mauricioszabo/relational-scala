package org.mauricioszabo.relational_scala

import org.mauricioszabo.relational_scala._
import org.mauricioszabo.relational_scala.results._

case class Selector(
    select: Seq[attributes.AttributeLike],
    from: Seq[tables.TableLike],
    where: comparissions.Comparission = null,
    group: Seq[attributes.AttributeLike] = Nil,
    having: comparissions.Comparission = null,
    join: Seq[_] = Nil,
    order: Seq[Partial] = Nil,
    connection: java.sql.Connection = null
  ) {

  def copy(
    select: Seq[attributes.AttributeLike] = select,
    from: Seq[tables.TableLike] = from,
    where: comparissions.Comparission = where,
    group: Seq[attributes.AttributeLike] = group,
    having: comparissions.Comparission = having,
    join: Seq[_] = join,
    order: Seq[Partial] = order,
    connection: java.sql.Connection = connection
  ) = new Selector( select, from, where, group, having, join, order, connection) with Query

  def results = {
    val statement = partial.createStatement(connection)
    new Promisse(statement.executeQuery)
  }

  lazy val partial = {
    var tuple = multiElementsQuery("SELECT ", select, ("", Nil))
    tuple = multiElementsQuery(" FROM ", from, tuple)
    if(where != null) tuple = whereQuery(" WHERE ", tuple)
    tuple = multiElementsQuery(" GROUP BY ", group, tuple)
    if(having != null) tuple = whereQuery(" HAVING ", tuple)
    // TODO: JOIN

    val(query, list) = tuple
    new PartialStatement(query, list)
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
}
