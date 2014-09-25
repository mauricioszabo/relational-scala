package relational.orm

class Property[A] {
  import relational.results.Attribute._

  private var default: A = _
  protected[orm] var possibleValue: () => Option[A] = { () => None }
  private var skipValidation = { () => false }

  protected[orm] val errorMessage: (String, Seq[Any]) = (
    "orm.mapping.errors." + getClass.getSimpleName.toLowerCase,
    Nil
  )

  def optionValue = possibleValue()

  def isPresent = possibleValue() != None

  def value = possibleValue() match {
    case Some(value) => value
    case None => default
  }

  def isValid = skipValidation() || validate

  protected def validate = true

  def validateIf(condition: => Boolean) = {
    skipValidation = {() => !condition}
    this
  }

  override def toString = getClass.getName+ "(" + (optionValue match {
    case Some(value) => "\"" + value.toString + "\": " + value.getClass.getName
    case None => "<null>"
  }) + ")"
}
