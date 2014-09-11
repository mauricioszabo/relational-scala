package test.relational
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{BeforeAndAfterAll, Suite}
import relational.Adapter

trait AdapterSetup extends BeforeAndAfterAll with ShouldMatchers { this: Suite =>
  override def beforeAll() {
    Adapter configure 'all
  }
}
