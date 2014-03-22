package relational.orm.config

object I18n {
  private val errors = Map(
    "orm.mapping.errors.formatted" -> "is in invalid format",
    "orm.mapping.errors.required" -> "is required",
    "orm.mapping.errors.validated" -> "does not conform to validation rules"
  )

  var translateAttribute = { s: Symbol => s.name.capitalize + " " }

  var translateError = { (error: String, attrs: Seq[Any]) =>
    attrs.zipWithIndex.foldLeft(errors(error)) { case(string, (param, index)) =>
      string.replaceAll("\\$"+index, param.toString)
    }
  }
}
