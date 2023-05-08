package actors

import actors.Consumer.Result
import actors.StoreShard.Command
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.cluster.sharding.typed.scaladsl.EntityTypeKey

object StoreShard {
  val TypeKey: EntityTypeKey[Command] = EntityTypeKey[Command]("StoreShard")

  sealed trait Command extends utils.Serializable

  case class Get(replyTo: ActorRef[Result], key: Seq[Byte]) extends Command

  case class Set(replyTo: ActorRef[Result], key: Seq[Byte], value: Seq[Byte]) extends Command

  def apply(wsid: String): Behavior[Command] = {
    Behaviors.setup { context =>
      new StoreShard(context, wsid)
    }
  }
}

class StoreShard(context: ActorContext[Command], wsid: String) extends AbstractBehavior[Command](context) {

  private val data = scala.collection.mutable.Map[Seq[Byte], Seq[Byte]]()

  override def onMessage(msg: Command): Behavior[Command] = msg match {
    case StoreShard.Get(replyTo, key) =>
      data.get(key) match {
        case Some(value) => replyTo ! Consumer.ConsumeGet(key, value)
        case None => replyTo ! Consumer.Error(key)
      }
      Behaviors.same
    case StoreShard.Set(replyTo, key, value) =>
      data.addOne(key, value)
      replyTo ! Consumer.ConsumeSet(key, value)
      Behaviors.same
  }
}
