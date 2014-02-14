package org.mauricioszabo.relational_scala.orm

import org.mauricioszabo.relational_scala.results.{Attribute => RAttribute}
import org.mauricioszabo.relational_scala.results.Attributes
import scala.language.implicitConversions
import scala.language.dynamics
import org.mauricioszabo.relational_scala.Selector

class DynamicAttr(a: => Attributes) extends Dynamic {
  def selectDynamic(key: String) = new Attribute(a.attribute get Symbol(key))
}

trait Mapping extends AssociationDefinition {
  protected val mappings: Map[Symbol, Any]
  protected var query: Selector = null

  def attributes = new Attributes(
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
    allProperties.map { p =>
      val property = p.invoke(this).asInstanceOf[Property[_]]
      if(property.isValid) {
        null
      } else {
        (Symbol(p.getName), property.errorMessage)
      }
    }.filter( _ != null )
  }

  override def toString() = {
    val old = super.toString
    val propertiesAndResults = allProperties.map { p =>
      p.getName + "=" + p.invoke(this).asInstanceOf[Property[_]].value
    }
    old + "("+ propertiesAndResults.mkString(", ") +")"
  }

  private def allProperties = {
    val propertyClass = classOf[Property[_]]

    getClass.getMethods.view.filter { e =>
      e.getParameterTypes.size == 0 && propertyClass.isAssignableFrom(e.getReturnType)
    }
  }
}
