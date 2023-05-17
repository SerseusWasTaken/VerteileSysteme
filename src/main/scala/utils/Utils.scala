package utils

import actors.StoreShard
import actors.StoreShard.TypeKey
import akka.actor.typed.scaladsl.ActorContext
import akka.cluster.sharding.typed.scaladsl.{ClusterSharding, Entity}
import com.typesafe.config.{Config, ConfigFactory}

object Utils {
  def getConfig(port: Int): Config = ConfigFactory
    .parseString(s"akka.remote.artery.canonical.port=$port")
    .withFallback(ConfigFactory.load())

  def byteSeqToString(key: Seq[Byte]): String = {
    new String(key match {
      case x: Seq[Integer] => x.map(s => s.byteValue()).toArray
      case x: Seq[Byte] => x.toArray
    })
  }

  def initializeSharding(context: ActorContext[_]): ClusterSharding = {
    val sharding = ClusterSharding(context.system)
    sharding.init(Entity(TypeKey) { entityContext =>
      StoreShard(entityContext.entityId)
    }.withRole("storeShard"))
    sharding
  }
}
