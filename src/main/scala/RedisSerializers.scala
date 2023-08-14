import Data.DomainStats
import scredis.serialization.{StringWriter, Writer}

object RedisSerializers {

  implicit object DomainStatsWriter extends Writer[DomainStats] {
    private val utf16StringWriter = new StringWriter("UTF-16")

    override def writeImpl(domainStats: DomainStats): Array[Byte] = {
      JsonSerializers.domainStatsWrites.writes(domainStats).toString().getBytes("UTF-16")
    }
  }
}