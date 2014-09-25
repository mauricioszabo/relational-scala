package tests

import org.scalatest._
import java.sql.DriverManager

trait DatabaseSetup extends BeforeAndAfterAll with BeforeAndAfterEach { this: Suite =>
  lazy val globalConnection = DriverManager.getConnection("jdbc:postgresql:postgres", "postgres", "")

  override def beforeEach() {
    globalConnection.prepareStatement("BEGIN TRANSACTION").executeUpdate

    globalConnection.prepareStatement("INSERT INTO scala_people VALUES(1, 'Foo', 17, '1982-10-12')").executeUpdate
    globalConnection.prepareStatement("INSERT INTO scala_people VALUES(2, 'Foo', 18, '1990-04-20')").executeUpdate
    globalConnection.prepareStatement("INSERT INTO scala_people VALUES(3, 'Bar', 17, '1987-07-29')").executeUpdate

    globalConnection.prepareStatement("INSERT INTO scala_addresses VALUES(1, 'Foo''s address', 1, '2010-10-10 09:00')").executeUpdate
    globalConnection.prepareStatement("INSERT INTO scala_addresses VALUES(2, 'Foo''s address 2', 1, '2010-10-10 08:00')").executeUpdate
    globalConnection.prepareStatement("INSERT INTO scala_addresses VALUES(3, 'Foo''s addesss', 2, '2010-10-10 08:00')").executeUpdate
  }

  override def afterEach() {
    //globalConnection.prepareStatement("DELETE FROM scala_people").executeUpdate
    globalConnection.prepareStatement("ROLLBACK").executeUpdate
  }

  override def beforeAll() {
    Class.forName("org.postgresql.Driver");
    globalConnection.prepareStatement("""CREATE TEMPORARY TABLE IF NOT EXISTS scala_people
      (id INTEGER PRIMARY KEY, name VARCHAR(255), age INTEGER, birth DATE)""").executeUpdate

    globalConnection.prepareStatement("""CREATE TEMPORARY TABLE IF NOT EXISTS scala_addresses
      (id INTEGER PRIMARY KEY, address VARCHAR(255), person_id INTEGER, changed_at TIME)""").executeUpdate
  }

  override def afterAll() {
  }
}
