package relational

import relational._
import relational.results._

case class Selector(
    select: clauses.Select,
    from: Seq[tables.TableLike],
    where: comparissions.Comparission = comparissions.None,
    group: Seq[attributes.AttributeLike] = Nil,
    having: comparissions.Comparission = comparissions.None,
    join: Seq[joins.Join] = Nil,
    order: Seq[Partial] = Nil,
    connection: java.sql.Connection = null,
    limit: Int = -1,
    offset: Int = -1
  ) extends FullSelect {

  def this(s: Selector) = this(s.select, s.from, s.where, s.group, s.having, s.join, s.order, s.connection, s.limit, s.offset)

  type Sql = (Adapter => String)

  def results = try {
    new Promisse(resultSet)
  } catch {
    case e: java.sql.SQLException => throw new java.sql.SQLException(
      "Error on SQL string: the offending code was:\n" +
      partial.toPseudoSQL + ";",
      e
    )
  }

  lazy val partial = {
    var tuple = constructSelect
    tuple = multiElementsQuery(" FROM ", from, tuple)
    join.foreach { j => tuple = constructQuery(tuple, " ", j) }

    if(where != comparissions.None) tuple = whereQuery(" WHERE ", tuple, where)
    tuple = multiElementsQuery(" GROUP BY ", group, tuple)
    if(having != comparissions.None) tuple = whereQuery(" HAVING ", tuple, having)

    if(!order.isEmpty) tuple = constructOrder(tuple)

    val(query, list) = tuple

    val pagination = new Pagination(query, list, limit=limit, offset=offset)
    pagination.partial
  }

  private def constructSelect = {
    val partial = select.partial
    (partial.sql, partial.attributes)
  }

  private def multiElementsQuery(before: String, elements: Seq[Partial], tuple: (Sql, Seq[Any])): (Sql, Seq[Any]) = {
    if(elements.isEmpty) return tuple
    val first::rest = elements.toList
    rest.foldLeft(constructQuery(tuple, before, first)) { constructQuery(_, ",", _) }
  }

  private def whereQuery(before: String, tuple: (Sql, Seq[Any]), where: comparissions.Comparission) = {
    constructQuery(tuple, before, where)
  }

  private def constructQuery(tuple: (Sql, Seq[Any]), before: String, element: Partial): (Sql, Seq[Any]) = {
    var (sql, list) = tuple
    val partial = element.partial
    def combineSql(a: Adapter) = sql(a) + before + partial.sql(a)
    list ++= partial.attributes
    (combineSql, list)
  }

  private def constructOrder(tuple: (Sql, Seq[Any])): (Sql, Seq[Any]) = {
    val (tsql, tattributes) = tuple
    val(sql, attributes) = order.foldLeft( (Seq[Sql](), tattributes) ) { case ((sql, attributes), partial) =>
      val sp = partial.partial
      val q = if(partial.isInstanceOf[FullSelect])
        { a: Adapter => "(" + sp.sql(a) + ")" }
      else
        sp.sql

      ( sql :+ q, attributes ++ sp.attributes)
    }

    def rsql(a: Adapter) = tsql(a) + " ORDER BY " + sql.map(s => s(a)).mkString("), (")
    (rsql, attributes)
  }
}
