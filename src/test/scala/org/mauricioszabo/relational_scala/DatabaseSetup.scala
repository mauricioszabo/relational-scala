import org.scalatest._
import java.sql.DriverManager

trait DatabaseSetup extends BeforeAndAfterAll with BeforeAndAfterEach { this: Suite =>
  lazy val connection = DriverManager.getConnection("jdbc:postgresql:postgres", "postgres", "")

  override def beforeEach() {
    connection.prepareStatement("BEGIN TRANSACTION").executeUpdate
    connection.prepareStatement("INSERT INTO scala_people VALUES(1, 'Foo', 17, '1982-10-12')").executeUpdate
    connection.prepareStatement("INSERT INTO scala_people VALUES(2, 'Foo', 18, '1990-04-20')").executeUpdate
    connection.prepareStatement("INSERT INTO scala_people VALUES(3, 'bar', 17, '1987-07-29')").executeUpdate
  }

  override def afterEach() {
    //connection.prepareStatement("DELETE FROM scala_people").executeUpdate
    connection.prepareStatement("ROLLBACK").executeUpdate
  }

  override def beforeAll() {
    Class.forName("org.postgresql.Driver");
    connection.prepareStatement("""CREATE TEMPORARY TABLE IF NOT EXISTS scala_people
      (id INTEGER PRIMARY KEY, name VARCHAR(255), age INTEGER, birth DATE)""").executeUpdate
  }

  override def afterAll() {
  }
}
