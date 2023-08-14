import DomainStatsFetcher._
import Messages._
import Requests._
import akka.actor.{ActorRef, Props}

import scala.concurrent.duration.DurationInt

object Scheduler {

//  private val redisActor: ActorRef = system.actorOf(Props(RedisActor), "redis-actor")

  def run(initialDelay: Int = 0, interval: Int = 5)(fn: () => Unit): Unit = {
    system.scheduler.scheduleWithFixedDelay(
      initialDelay = initialDelay.seconds,
      delay = interval.minutes)(runnable = () => fn())
  }
}
