package relational

import scala.collection.mutable.{Map, SynchronizedMap, HashMap}

object Adapter {
  type Function = (Partial, Seq[Partial]) => (String, Seq[Any])
  type ListOfFunctions = (Symbol, Function)

  type HashMapType = HashMap[Symbol, Function] with SynchronizedMap[Symbol, Function]
  val functions = new HashMap[Symbol, HashMapType] with SynchronizedMap[Symbol, HashMapType]

  private val DEFAULT_DRIVER = 'all
  private var driver = DEFAULT_DRIVER

  def configure(driver: Symbol = DEFAULT_DRIVER) {
    this.driver = driver
    attributes.ConfigFunctions
  }

  def currentDriver = driver

  def defineFunctionN(name: Symbol, strings: (Symbol, String)*) {
    val functions = strings.map { case(driver, string) =>
      val function = { (self: Partial, params: Seq[Partial]) =>
        val partialsList = self :: params.toList
        val matchList = """\$(\d+)""".r.findAllMatchIn(string)

        matchList.foldLeft(string -> List[Any]()) { case((query, attributes), matchElement) =>
          val index = matchElement.group(1).toInt
          val partial = partialsList(index).partial

          val q = query.replaceAll("\\$" + index, partial.query)
          val a = attributes ++ partial.attributes
          (q, a)
        }
      }
      (driver, function)
    }
    defineFunction(name, functions: _*)
  }

  def defineFunction(name: Symbol, functions: ListOfFunctions*) {
    functions.foreach { case(driver, body) =>
      Adapter.addCustomFunction(name, driver, body)
    }
  }

  def addCustomFunction(name: Symbol, driver: Symbol, function: Function) {
    val entry = getDriverEntry(driver)
    entry(name) = function
  }

  def getFunction(name: Symbol): Function = getFunctionForDriver(driver, name)

  private def getFunctionForDriver(driver: Symbol, name: Symbol): Function = {
    getDriverEntry(driver) get name match {
      case Some(function) => function
      case None if(driver == DEFAULT_DRIVER) => { (p: Partial, a: Seq[Partial]) => ("", Nil) }
      case None => getFunctionForDriver(DEFAULT_DRIVER, name)
    }
  }

  private def getDriverEntry(driver: Symbol) = functions get driver match {
    case Some(entry) => entry
    case None =>
      val entry = new HashMap[Symbol, Function] with SynchronizedMap[Symbol, Function]
      functions(driver) = entry
      entry
  }
}
