import org.jsoup.Jsoup
import play.api.libs.json.JsValue

import scala.annotation.tailrec

object ResponseUtils {
  @tailrec
  def jsonFieldByKeys(json: JsValue, attributes: List[String]): JsValue = {
    attributes match {
      case Nil => json
      case head :: tail => jsonFieldByKeys(json(head), tail)
    }
  }

  def nextData(html: String): String = {
    Jsoup.parse(html).select("#__NEXT_DATA__").html()
  }
}
