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
      j <- partialTo(join, " ", " ")
      o <- partialTo(order, " ORDER BY ")
    } yield (
      s.query + f.query + j.query + w.query + g.query + h.query + o.query,
      s.params ++ f.params ++ j.params ++ w.params ++ g.params ++ h.params ++ o.params
    )

    new Pagination(queries, limit=limit, offset=offset).partial
  }

  private def partialTo(partials: Seq[Partial], string: String, separator: String = ", "): PartialStatement = {
    partials.toList match {
      case Nil => nilPartial
      case head::Nil => head.partial.map { p => string + p.query -> p.params }
      case head::tail => for {
        p1 <- head.partial
        p2 <- partialTo(tail, separator, separator)
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
}
