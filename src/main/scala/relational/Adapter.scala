package relational

import scala.collection.mutable.{Map, SynchronizedMap, HashMap}

class Adapter(d: Symbol) {
  val DEFAULT_DRIVER = 'all
  private var driver = d

  def this() = this('all)

  def configure(driver: Symbol = DEFAULT_DRIVER) {
    this.driver = driver
  }

  def currentDriver = driver
}

object Adapter extends Adapter {
  implicit val default = {
    this
  }
}
