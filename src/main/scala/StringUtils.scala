object StringUtils {
  def tabbedString(text: String, tabs: Int): String = {
    text.split("\n")
      .map(row => s"""${"\t" * tabs}$row""")
      .mkString("", "\n", "")
  }

}
