package org.mauricioszabo.relational_scala.orm

import org.mauricioszabo.relational_scala.orm.config.I18n

class MaybeValid {
}

case class Valid(record: Mapping) extends MaybeValid {
}

case class Invalid(record: Mapping, errors: Errors) extends MaybeValid {
}

class Errors(map: Map[Symbol, Seq[(String, Seq[Any])]]) {
  def apply(key: Symbol) = map(key)

  def list = {
    map.flatMap { case(attribute, errorsWithParams) =>
      errorsWithParams.map { errorWithParams =>
        val (error, params) = errorWithParams
        I18n.translateAttribute(attribute) + I18n.translateError(error, params)
      }
    }
  }
}
