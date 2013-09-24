package org.mauricioszabo.relational_scala.orm.mapper

import org.mauricioszabo.relational_scala.results.Attributes

trait IdentityMap[A] {
  def mapTo(attributes: Attributes): A
}
