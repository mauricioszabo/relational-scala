import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

import org.mauricioszabo.relational_scala._
import java.sql.DriverManager

class SelectorTest extends FlatSpec with ShouldMatchers {
  val people = new tables.Table("people")
  val selector = new Selector

  Class.forName("org.sqlite.JDBC");
  val connection = DriverManager.getConnection("jdbc:sqlite::memory:")

  //Creation of databases
  connection.prepareStatement("""CREATE TABLE people
    (id INTEGER PRIMARY KEY, name VARCHAR(255), age INTEGER)""").executeUpdate

  connection.prepareStatement("INSERT INTO people VALUES(1, 'Foo', 17)").executeUpdate
  connection.prepareStatement("INSERT INTO people VALUES(2, 'Foo', 18)").executeUpdate
  connection.prepareStatement("INSERT INTO people VALUES(3, 'bar', 17)").executeUpdate

  "Selector" should "search for records in a database" in {
    val results = selector from (people)
    results.size should be === 3
  }
}
