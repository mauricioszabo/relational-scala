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
    order: Seq[orders.Ordering] = Nil,
    connection: java.sql.Connection = null,
    limit: Int = -1,
    offset: Int = -1
  ) extends FullSelect {

  def this(s: Selector) = this(s.select, s.from, s.where, s.group, s.having, s.join, s.order, s.connection, s.limit, s.offset)

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
    val queries = for {
      s <- select.partial
      f <- partialTo(from, " FROM ")
      w <- partialTo(where,  " WHERE ")
      g <- partialTo(group, " GROUP BY ")
      h <- partialTo(having, " HAVING ")
      j <- partialTo(join, " ")
      o <- partialTo(order, " ORDER BY ")
    } yield (
      s.query + f.query + w.query + g.query + h.query + j.query + o.query,
      s.params ++ f.params ++ w.params ++ g.params ++ h.params ++ j.params ++ o.params
    )

    new Pagination(queries, limit=limit, offset=offset).partial
  }

  private def partialTo(partials: Seq[Partial], string: String): PartialStatement = {
    partials.toList match {
      case Nil => nilPartial
      case head::Nil => head.partial.map { p => string + p.query -> p.params }
      case head::tail => for {
        p1 <- head.partial
        p2 <- partialTo(tail, ", ")
      } yield (string + p1.query + p2.query, p1.params ++ p2.params)
    }
  }

  private def partialTo(comp: comparissions.Comparission, string: String): PartialStatement = {
    if(comp equivalentTo comparissions.None)
      nilPartial
    else
      comp.partial.map { c => string + c.query -> c.params }
  }

  private val nilPartial = PartialStatement { a => "" -> Seq() }

  //lazy val partial = select.map { select =>
  //  val tuple = (select.query, select.params)
  //  tuple = multiElementsQuery(" FROM ", from, tuple)
  //  join.foreach { j => tuple = constructQuery(tuple, " ", j) }

  //  if(where != comparissions.None) tuple = whereQuery(" WHERE ", tuple, where)
  //  tuple = multiElementsQuery(" GROUP BY ", group, tuple)
  //  if(having != comparissions.None) tuple = whereQuery(" HAVING ", tuple, having)

  //  if(!order.isEmpty) tuple = constructOrder(tuple)

  //  val(query, list) = tuple

  //  val pagination = new Pagination(query, list, limit=limit, offset=offset)
  //  pagination.partial
  //}

  //private def multiElementsQuery(before: String, elements: Seq[Partial], tuple: (String, Seq[Any])): (String, Seq[Any]) = {
  //  if(elements.isEmpty) return tuple
  //  val first::rest = elements.toList
  //  rest.foldLeft(constructQuery(tuple, before, first)) { constructQuery(_, ",", _) }
  //}

  //private def whereQuery(before: String, tuple: (String, Seq[Any]), where: comparissions.Comparission) = {
  //  constructQuery(tuple, before, where)
  //}

  //private def constructQuery(tuple: (String, Seq[Any]), before: String, element: Partial): (String, Seq[Any]) = {
  //  var (sql, list) = tuple
  //  val partial = element.partial
  //  def combineString(a: Adapter) = sql(a) + before + partial.sql(a)
  //  list ++= partial.attributes
  //  (combineString, list)
  //}

  //private def constructOrder(tuple: (String, Seq[Any])): (String, Seq[Any]) = {
  //  val (tsql, tattributes) = tuple
  //  val(sql, attributes) = order.foldLeft( (Seq[String](), tattributes) ) { case ((sql, attributes), partial) =>
  //    val sp = partial.partial
  //    val q = if(partial.isInstanceOf[FullSelect])
  //      { a: Adapter => "(" + sp.sql(a) + ")" }
  //    else
  //      sp.sql

  //    ( sql :+ q, attributes ++ sp.attributes)
  //  }

  //  def rsql(a: Adapter) = tsql(a) + " ORDER BY " + sql.map(s => s(a)).mkString("), (")
  //  (rsql, attributes)
  //}
}
