package relational

protected object Random {
  def generate(size: Int) = {
    val number = "[0-9]".r
    val random = scala.util.Random
    var string = ""
    while(string.isEmpty) {
      string = number.replaceAllIn(random.alphanumeric.take(5).mkString, "")
    }
    string
  }
}
