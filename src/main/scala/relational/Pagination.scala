package relational

class Pagination(sql: Adapter => String, attributes: Seq[Any],
    offset: Int = -1, limit: Int = -1) extends Partial {

  lazy val partial = {
    var limitSQL = this.sql
    if(limit >= 0) limitSQL = { a: Adapter => sql(a) + " LIMIT " + limit }
    var offsetSQL = limitSQL
    if(offset >= 0) offsetSQL = { a: Adapter => limitSQL(a) + " OFFSET " + offset }

    new PartialStatement(attributes)(offsetSQL)
  }
}
