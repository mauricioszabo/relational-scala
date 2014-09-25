package relational.attributes

import relational.Adapter

object Escape {
  def apply(adapter: Adapter, name: String): String = adapter.currentDriver match {
    case 'mysql => "`" + name.replaceAll("`", "``") + "`"
    case _ => "\"" + name.replaceAll("\"", "\"\"") + "\""
  }
}
