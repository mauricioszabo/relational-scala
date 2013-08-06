package org.mauricioszabo.relational_scala

case class PartialStatement(query: String, attributes: Seq[Any]) {
  lazy val toPseudoSQL = normalizedAttrs.foldLeft(query) { (query, attribute) =>
    query.replaceFirst("\\?", attribute)
  }

  private lazy val normalizedAttrs = attributes map normalize

  private def normalize(attribute: Any) = attribute match {
    case str: String => "'" + str.replaceAll("'", "''") + "'"
    //case date: Date => "'" + date.toString + "'"
    //case time: Time => "'" + time.utc + "'"
    case _ => attribute.toString
  }

}
