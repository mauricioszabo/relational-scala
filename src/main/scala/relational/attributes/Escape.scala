package relational.attributes

import relational.Adapter

object Escape {
  def apply(name: String)(implicit adapter: Adapter) = adapter.currentDriver match {
    case 'mysql => "`" + name.replaceAll("`", "``") + "`"
    case _ => "\"" + name.replaceAll("\"", "\"\"") + "\""
  }
}