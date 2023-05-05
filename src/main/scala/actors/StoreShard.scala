package actors

import actors.StoreShard.Command
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.cluster.sharding.typed.ShardingEnvelope
import akka.cluster.sharding.typed.scaladsl.{ClusterSharding, Entity, EntityTypeKey}

object StoreShard {
  val TypeKey: EntityTypeKey[Command] = EntityTypeKey[Command]("StoreShard")
  sealed trait Command extends utils.Serializable

  case class PrintName() extends Command

  case class Get(key: Seq[Byte]) extends Command
  case class Set(key: Seq[Byte], value: Seq[Byte]) extends Command
  def apply(wsid: String): Behavior[Command] = {
    Behaviors.setup { context =>
      new StoreShard(context, wsid)
    }
  }

  def createShard(system: ActorSystem[_]): ActorRef[ShardingEnvelope[Command]] =
    ClusterSharding(system).init(Entity(TypeKey) { entityContext =>
      StoreShard(entityContext.entityId)
  }.withRole("storeShards"))
}

class StoreShard(context: ActorContext[Command], wsid: String) extends AbstractBehavior[Command](context) {

  private val data = scala.collection.mutable.Map[Seq[Byte], Seq[Byte]]()
  override def onMessage(msg: Command): Behavior[Command] = msg match {
    case StoreShard.PrintName() =>
      context.log.info(s"My meta info is : ${context.self.path} and i have id $wsid")
      Behaviors.same
    case StoreShard.Get(key) =>
      context.log.info(s"Got value: ${data(key)} from key: $key")
      Behaviors.same
    case StoreShard.Set(key, value) =>
      data.addOne(key, value)
      context.log.info(s"Added key/value: $key, $value ")
      Behaviors.same
  }
}
