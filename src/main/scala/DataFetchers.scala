import Data._
import JsonSerializers._
import Requests._
import ResponseUtils._
import akka.http.scaladsl.model.HttpHeader
import org.jsoup.Jsoup
import play.api.libs.json.Json

import scala.concurrent.Future
import scala.util.Try

object Urls {
  private val trustpilotUrl = "https://www.trustpilot.com"
  val categoriesUrl = s"$trustpilotUrl/categories"
  private val url = "https://web.vstat.info"

  def vstatDomainUrl(domain: String): String = s"$url/$domain"

  def mostRecentDomainsUrlByCategory(category: String): String = s"$categoriesUrl/$category?sort=latest_review"
}

import Urls._

object DomainStatsFetcher {
  def fetchDomainsStats(vstatHeaders: List[HttpHeader] = Nil): Future[List[DomainStats]] = {
    val categoriesFuture: Future[List[ParentCategory]] = singleRequest(categoriesUrl).map {
      htmlResponse => {
        val parsedJson = Json.parse(nextData(htmlResponse))
        jsonFieldByKeys(parsedJson, List("props", "pageProps", "categories"))
          .as[Seq[ParentCategory]].toList
      }
    }
    val domainsFuture: Future[List[Domain]] = {
      categoriesFuture.map { categories =>
        val categoriesId = categories.map(_.id)
        val requests = categoriesId.map(category => mostRecentDomainsUrlByCategory(category))
        parallelRequests(requests).map { htmlResponses =>
          htmlResponses.flatMap { singleResponse =>
            jsonFieldByKeys(Json.parse(nextData(singleResponse)), List("props", "pageProps", "recentlyReviewedBusinessUnits"))
              .as[Seq[Domain]].toList
          }
        }
      }.flatten
    }
    val domainsWithStoreFuture = domainsFuture
      .map(_.filter(domain => domain.categories.exists(_.id.contains("store"))))
    val monthlyVisitsByDomainFuture: Future[List[(Option[Int], String)]] = {
      domainsWithStoreFuture.map { domains =>
        val domainsName = domains.map(_.identifyingName)
        val requests = domainsName.map(domain => vstatDomainUrl(domain))
        parallelRequests(requests, vstatHeaders).map { htmlResponses =>
          htmlResponses.map { htmlResponse =>
            val potentialMonthlyVisits = Jsoup.parse(htmlResponse).select("#MONTHLY_VISITS")
            val monthlyVisits: Option[Int] =
              Try(potentialMonthlyVisits.attr("data-smvisits").toInt).toOption
            monthlyVisits
          }.zip(domainsName)
        }
      }.flatten
    }
    val domainsStatsFuture: Future[List[DomainStats]] = {
      for {
        domainsWithStore: List[Domain] <- domainsWithStoreFuture
        monthlyVisitsByDomain: List[(Option[Int], String)] <- monthlyVisitsByDomainFuture
      } yield {
        domainsWithStore.map { domain =>
          val monthlyVisits = monthlyVisitsByDomain.find(_._2 == domain.identifyingName).map(_._1).get
          DomainStats(domain.id, domain.identifyingName, domain.latestReview, monthlyVisits, domain.numberOfReviews)
        }
      }
    }
    domainsStatsFuture
  }
}
