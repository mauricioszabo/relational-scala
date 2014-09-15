package relational.joins

import relational._

trait Join extends Partial {
  def table: tables.TableLike
  def condition: comparissions.Comparission

  protected def newPartial(table: tables.TableLike, comparission: comparissions.Comparission, text: String) = {
    val tablePartial = table.partial
    val condPartial = comparission.partial

    new PartialStatement(text + " " + tablePartial.query + " ON " + condPartial.query,
      tablePartial.attributes ++ condPartial.attributes)
  }
}
