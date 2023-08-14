import ArgsParser.optionsOrExit
import DomainStatsFetcher.fetchDomainsStats
import akka.http.scaladsl.model.headers
import Requests.system
import Messages._
import akka.actor.Props

object Main {
  def main(args: Array[String]): Unit = {
    val options = optionsOrExit(args, new ArgsOptions())
    val cookies = options.vstat_session match {
      case "" => Nil
      case cookieValue => List(headers.Cookie(name = "vstat_session", value = cookieValue))
    }
    val redis = system.actorOf(Props(RedisActor))
    Scheduler.run() { () =>
      redis ! Push(fetchDomainsStats(cookies))
      if (options.verbose) redis ! Pull(10)
    }
  }
}
