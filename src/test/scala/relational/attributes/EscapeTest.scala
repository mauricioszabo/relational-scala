package test.relational.attributes

import relational._
import relational.attributes._
import org.scalatest._

class EscapeTest extends WordSpec with matchers.ShouldMatchers with BeforeAndAfterEach {
  "Escape attributes" should {
    "escape with commas" in {
      val a = new Adapter('all)
      Escape(a, "some_value") should be === "\"some_value\""
      Escape(a, "some\"value") should be === "\"some\"\"value\""
    }

    "escape in MySQL" in {
      val a = new Adapter('mysql)
      Escape(a, "some_value") should be === "`some_value`"
      Escape(a, "some`value") should be === "`some``value`"
    }
  }
}
