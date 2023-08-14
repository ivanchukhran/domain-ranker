import org.kohsuke.args4j.{CmdLineException, CmdLineParser, Option}
import scala.util.Try
import scala.collection.JavaConverters._
import org.kohsuke.args4j.NamedOptionDef


trait Helpable {
  @Option(name = "--help", aliases = Array("-h"), usage = "show help")
  var help = false
}

class ArgsOptions extends Helpable {
  @Option(name = "--vstat-session", aliases = Array("-vs"), usage = "web.vstat.com session to get precise domain traffic")
  var vstat_session: String = ""

  @Option(name = "--verbose", aliases = Array("-v"), usage = "whether to print top 10 domains or not")
  var verbose: Boolean = true
}

object ArgsParser {
  def optionsOrExit[T <: Helpable](args: Array[String], options: T): T = {
    val parser = new CmdLineParser(options)
    if (options.help) {
      parser.printUsage(System.err)
      sys.exit(0)
    }
    try {
      parser.parseArgument(args: _*)
    } catch {
      case e: CmdLineException =>
        // required arguments not present, or parsing error.  Check to see if help was requested
        val help = e.getParser.getOptions.asScala.find { o =>
          o.option match {
            case n: NamedOptionDef => n.name == "--help"
            case _ => false
          }
        }
        for (h <- help if h.setter.asFieldSetter.getValue == true) {
          parser.printUsage(System.err)
          sys.exit(0)
        }
        // missing required var and help not requested
        System.err.println(e.getMessage)
        parser.printUsage(System.err)
        sys.exit(1)
    }
    options
  }
}

