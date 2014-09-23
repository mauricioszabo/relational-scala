package relational.comparissions

import relational.{Adapter, PartialStatement}

case class Or(protected val comparissions: Vector[Comparission]) extends Comparission {
  lazy val partial = PartialStatement { adapter =>
    val partials = comparissions.view.map { _.partial.tuple(adapter) }
    val queries = "(" + partials.map { p => p._1 }.mkString(" OR ") + ")"
    val attributes = partials.flatMap { _._2 }
    queries -> attributes
  }
}
