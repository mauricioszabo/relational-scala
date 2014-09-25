package relational

class Pagination(partialStatement: PartialStatement,
    offset: Int = -1, limit: Int = -1) extends Partial {

  lazy val partial = partialStatement.map { p =>
    var query = p.query
    if(limit >= 0) query += " LIMIT " + limit
    if(offset >= 0) query += " OFFSET " + offset
    query -> p.params
  }
}
