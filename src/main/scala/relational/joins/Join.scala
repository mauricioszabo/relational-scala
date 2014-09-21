package relational.joins

import relational._

trait Join extends Partial {
  def table: tables.TableLike
  def condition: comparissions.Comparission

  protected def newPartial(table: tables.TableLike, comparission: comparissions.Comparission, text: String) = {
    val tablePartial = table.partial
    val condPartial = comparission.partial
    val fn = {a: Adapter =>
      text + " " + tablePartial.sql(a) + " ON " + condPartial.sql(a)
    }

    new PartialStatement(tablePartial.attributes ++ condPartial.attributes)(fn)
  }
}

object Join {
  def unapply(obj: Join): Option[(tables.TableLike, comparissions.Comparission, Symbol)] = {
    obj match {
      case LeftJoin(table, comparission) => Some((table, comparission, 'left))
      case RightJoin(table, comparission) => Some((table, comparission, 'right))
      case InnerJoin(table, comparission) => Some((table, comparission, 'inner))
    }
  }
}
