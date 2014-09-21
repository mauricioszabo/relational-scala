package relational

class Pagination(sql: Adapter => String, attributes: Seq[Any],
    offset: Int = -1, limit: Int = -1) extends Partial {

  lazy val partial = {
    var sql = this.sql
    if(limit >= 0) sql = { a: Adapter => sql(a) + " LIMIT " + limit }
    if(offset >= 0) sql = { a: Adapter => sql(a) + " OFFSET " + offset }

    new PartialStatement(attributes)(sql)
  }
}
