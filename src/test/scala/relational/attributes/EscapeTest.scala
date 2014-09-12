package test.relational.attributes

import relational._
import relational.attributes._
import org.scalatest._

class EscapeTest extends WordSpec with matchers.ShouldMatchers with BeforeAndAfterEach {
  "Escape attributes" should {
    "escape with commas" in {
      Adapter configure 'all
      Escape("some_value") should be === "\"some_value\""
      Escape("some\"value") should be === "\"some\"\"value\""
    }

    "escape in MySQL" in {
      Adapter configure 'mysql
      Escape("some_value") should be === "`some_value`"
      Escape("some`value") should be === "`some``value`"
    }
  }

  override def afterEach {
    Adapter configure 'all
  }
}
