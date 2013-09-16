package org.mauricioszabo.relational_scala.orm

import scala.language.implicitConversions
import org.mauricioszabo.relational_scala.results.{Attribute => Attr}
import scala.reflect.ClassTag
import scala.language.higherKinds

class Attribute(maybeAttr: => Option[Attr]) {
  def as[A[B] <: Property[B], B](implicit ct: ClassTag[A[B]], ev: Attr => B) = {
    val newProperty = ct.runtimeClass.newInstance.asInstanceOf[A[B]]
    newProperty.possibleValue = { () => getValue[B] }
    newProperty
  }

  private def getValue[A](implicit ev: Attr => A): Option[A] = try {
    maybeAttr match {
      case Some(value) if(value.isNull) => None
      case Some(value) => Some(ev(value))
      case None => None
      }
  } catch {
    case _: NumberFormatException => None
  }
}
