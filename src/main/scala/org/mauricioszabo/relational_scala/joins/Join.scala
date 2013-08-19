package org.mauricioszabo.relational_scala.joins

import org.mauricioszabo.relational_scala._

trait Join extends Partial {
  protected def newPartial(table: tables.TableLike, comparission: comparissions.Comparission, text: String) = {
    val tablePartial = table.partial
    val condPartial = comparission.partial

    new PartialStatement(text + " " + tablePartial.query + " ON " + condPartial.query,
      tablePartial.attributes ++ condPartial.attributes)
  }
}
