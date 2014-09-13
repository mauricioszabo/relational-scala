package test.relational.attributes

import relational._
import relational.attributes._
import org.scalatest._

class EscapeTest extends WordSpec with matchers.ShouldMatchers with BeforeAndAfterEach {
  "Escape attributes" should {
    "escape with commas" in {
      implicit val a = new Adapter('all)
      Escape("some_value") should be === "\"some_value\""
      Escape("some\"value") should be === "\"some\"\"value\""
    }

    "escape in MySQL" in {
      implicit val a = new Adapter('mysql)
      Escape("some_value") should be === "`some_value`"
      Escape("some`value") should be === "`some``value`"
    }
  }
}
