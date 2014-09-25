package test.relational

import org.scalatest._
import relational.{attributes => attrs}
import relational._

class AdapterTest extends WordSpec with matchers.ShouldMatchers with BeforeAndAfterEach {
  implicit val adapter = new Adapter
  adapter.configure('all)
}
