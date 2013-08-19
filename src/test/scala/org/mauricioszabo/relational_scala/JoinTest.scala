import org.scalatest._
import org.mauricioszabo.relational_scala._
import org.mauricioszabo.relational_scala.joins._

class JoinTest extends FlatSpec with matchers.ShouldMatchers {
  val people = new tables.Table("people")
  val addresses = new tables.Table("addresses")

  "Joins" should "create left joins" in {
    val join = new LeftJoin(addresses, people('id) == addresses('people_id))
    join.partial.toPseudoSQL should be === "LEFT JOIN addresses ON people.id = addresses.people_id"
  }

  it should "create right joins" in {
    val join = new RightJoin(addresses, people('id) == addresses('people_id))
    join.partial.toPseudoSQL should be === "RIGHT JOIN addresses ON people.id = addresses.people_id"
  }

  it should "create inner joins" in {
    val join = new InnerJoin(addresses, people('id) == addresses('people_id))
    join.partial.toPseudoSQL should be === "INNER JOIN addresses ON people.id = addresses.people_id"
  }
}
