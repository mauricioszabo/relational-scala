package tests.results

import org.scalatest._
import org.scalatest.matchers.ShouldMatchers

import relational.results._

class AttributeTest extends WordSpec with ShouldMatchers {
  val string = new Attribute("10.1")
  val number = new NumericAttribute(10.1)

  "Attribute" should {
    "cast to string" in {
      string.value should be === "10.1"
    }

    "cast to number" in {
      intercept[NumberFormatException] { string.as(Int) }
      string.as(Double) should be === 10.1
      intercept[NumberFormatException] { string.as(Long) }
      intercept[NumberFormatException] { string.as(Short) }
    }
  }

  "NumericAttribute" should {
    "cast to string" in {
      number.value should be === "10.1"
    }

    "cast to number" in {
      number.as(Int) should be === 10
      number.as(Double) should be === 10.1
      number.as(Long) should be === 10
      number.as(Short) should be === 10
    }
  }

  "Automatic conversion" should {
    "cast to NumericAttribute" in {
      Attribute(10.1).asInstanceOf[NumericAttribute[Float]] should be === number
    }
  }

  "Implicit conversion" should {
    "cast to string" in {
      val s: String = string
      s should be === "10.1"
    }

    "cast to number" in {
      var n: Double = string
      n should be === 10.1

      val i: Int = number
      i should be === 10
    }
  }
}

