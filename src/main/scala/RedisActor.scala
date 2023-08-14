import Data.DomainStats
import JsonSerializers._
import Messages._
import Requests.executionContext
import akka.actor.Actor
import play.api.libs.json.Json
import scredis.Redis

import scala.util.Success

object RedisActor extends Actor {
  private val redis = Redis()

  override def receive: Receive = {
    case Push(newDomainsStatsFuture) =>
      for {
        newDomainsStats <- newDomainsStatsFuture
        domainsCache <- redis.get("domains")
      } yield {
        val oldDomainsStats: List[DomainStats] = Json.parse(domainsCache.getOrElse("[]")).as[Seq[DomainStats]].toList
        val existing = oldDomainsStats.filter { oldDomainStats =>
          newDomainsStats.exists(_.identifyingName == oldDomainStats.identifyingName)
        }
        val oldNotExisting = oldDomainsStats.diff(existing)
        val mergedDomainsStats = newDomainsStats.map { newDomainStats =>
          existing.find(_.identifyingName == newDomainStats.identifyingName) match {
            case Some(oldDomainStats) =>
              val newLatestReviewsCount = newDomainStats.numberOfReviews - oldDomainStats.numberOfReviews
              newDomainStats.copy(latestReviewCount = newLatestReviewsCount)
            case None => newDomainStats
          }
        }
        val newDomainsStatsCache = Json.toJson(mergedDomainsStats ++ oldNotExisting).toString()
        redis.set("domains", newDomainsStatsCache)
      }
    case Pull(n_first) =>
      redis.get("domains").onComplete {
        case Success(value) =>
          val domainsStats: List[DomainStats] = Json.parse(value.getOrElse("[]")).as[Seq[DomainStats]].toList
          println("Top 10 domains: ")
          domainsStats
            .take(n_first)
            .zipWithIndex
            .foreach { case (value, key) => println(s"#$key $value") }
        case _ => println("No domains in cache")
      }
  }
}