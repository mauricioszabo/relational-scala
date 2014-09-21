package relational.attributes

import relational.Adapter

object Escape {
  def apply(adapter: Adapter, name: String): String = apply(name)(adapter)

  def apply(name: String)(implicit adapter: Adapter): String = adapter.currentDriver match {
    case 'mysql => "`" + name.replaceAll("`", "``") + "`"
    case _ => "\"" + name.replaceAll("\"", "\"\"") + "\""
  }
}
