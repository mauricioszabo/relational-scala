package relational.orm.mapper

import relational.QueryBase
import relational.Selector

trait ScopeDefinition[A] {
  type Q = QueryBase[A] with SelectorCompliance
  protected var definedScopes = Map[Symbol, Q => A]()

  protected def defScope(name: Symbol)(fn: Q => A) {
    assertFree(name)
    definedScopes += (name -> fn )
  }

  private def assertFree(name: Symbol) {
    if(definedScopes.get('name) != None) throw new RuntimeException("scope's name is already in use")
  }
}

trait ScopeFinder[A] { self: QueryBase[A] with SelectorCompliance =>
  protected val scopes: Map[Symbol, QueryBase[A] with SelectorCompliance => A]

  def find(scope: Symbol): A = scopes(scope)(this)
}
