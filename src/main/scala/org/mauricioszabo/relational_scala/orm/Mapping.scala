package org.mauricioszabo.relational_scala.orm

import org.mauricioszabo.relational_scala.results.{Attribute => RAttribute}
import org.mauricioszabo.relational_scala.results.Attributes
import scala.language.implicitConversions
import scala.language.dynamics

class DynamicAttr(a: => Attributes) extends Dynamic {
  def selectDynamic(key: String) = new Attribute(a.attribute get Symbol(key))
}

trait Mapping {
  protected val mappings: Seq[(Symbol, Any)]

  lazy val attributes = new Attributes(
    mappings.map { case(key, value) => (key, RAttribute(value)) }
  )

  def attr = new DynamicAttr(attributes)

  def attr(key: Symbol) = new Attribute( attribute get key )
  def attribute = attributes.attribute

  def isValid = {
    val propertyClass = classOf[Property[_]]
    val properties = getClass.getMethods.view.filter { e =>
      e.getParameterTypes.size == 0 && propertyClass.isAssignableFrom(e.getReturnType)
    }
    val invalid = properties.map(_.invoke(this).asInstanceOf[Property[_]].isValid).contains(false)
    !invalid
  }

  def valid = {
    val errors = allErrors.foldLeft(Map[Symbol, Seq[(String, Seq[Any])]]()) { (map, element) =>
      val (attribute, error) = element

      val newErrors = map.get(attribute) match {
        case Some(errors) => errors :+ error
        case None => Seq(error)
      }

      map ++ Map(attribute -> newErrors)
    }
    if(errors.isEmpty) {
      Valid(this)
    } else {
      Invalid(this, new Errors(errors))
    }
  }

  private def allErrors = {
    val propertyClass = classOf[Property[_]]

    val properties = getClass.getMethods.view.filter { e =>
      e.getParameterTypes.size == 0 && propertyClass.isAssignableFrom(e.getReturnType)
    }

    properties.map { p =>
      val property = p.invoke(this).asInstanceOf[Property[_]]
      if(property.isValid) {
        null
      } else {
        (Symbol(p.getName), property.errorMessage)
      }
    }.filter( _ != null )
  }
}
