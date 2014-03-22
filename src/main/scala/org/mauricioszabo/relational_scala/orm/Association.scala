package relational.orm

import relational.orm.mapper.LazyResultSet
import scala.language.higherKinds

trait AssociationDefinition {
  def is[B <: Association[A], A] = new AssociationDefinerHelper[B, A]
}

class AssociationDefinerHelper[B <: Association[A], A] {
  //def query(query: Selector => LazyResultSet[A]) = {

  //}
}

trait Association[A] {
  def values: LazyResultSet[A]
  def retrieve: LazyResultSet[A]
}

class Many[A] extends Association[A] {
  def values: LazyResultSet[A] = null
  def retrieve: LazyResultSet[A] = null
}
