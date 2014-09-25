package relational.joins

import relational._

trait Join extends Partial {
  def table: tables.TableLike
  def condition: comparissions.Comparission

  protected def newPartial(table: tables.TableLike, comparission: comparissions.Comparission, text: String) = for {
    t <- table.partial
    c <- comparission.partial
  } yield (text + " " + t.query + " ON " + c.query, t.params ++ c.params)
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
