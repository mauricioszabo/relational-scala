package org.mauricioszabo.relational_scala

class Pagination(query: String, attributes: Seq[Any],
    offset: Int = -1, limit: Int = -1) extends Partial {

  lazy val partial = {
    var query = this.query
    if(limit >= 0) query = query + " LIMIT " + limit
    if(offset >= 0) query = query + " OFFSET " + offset

    new PartialStatement(query, attributes)
  }
}
