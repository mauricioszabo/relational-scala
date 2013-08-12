import org.scalatest._
import org.mauricioszabo.relational_scala._

class JoinTest extends FlatSpec with matchers.ShouldMatchers {
  val people = new tables.Table("people")
  val addresses = new tables.Table("addresses")

  "Joins" should "create left joins" in {
    //val join = new LeftJoin(addresses, people('id) == addresses('people_id))
    //table.partial.toPseudoSQL should be === "LEFT JOIN addresses ON people.id = addresses.people_id"
  }
}
