package relational.comparissions

import relational.PartialStatement

class Not(comparission: Comparission) extends Comparission {
  lazy val partial = new PartialStatement("NOT(" + statement.query + ")", statement.attributes)

  private lazy val statement = comparission.partial
}
