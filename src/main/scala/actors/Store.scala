package actors

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import Consumer.{Numbers, Result}

object Store {
  sealed trait Command
  case class Get(replyTo: ActorRef[Result], key: Seq[Byte]) extends Command
  case class Set(replyTo: ActorRef[Result], key: Seq[Byte], value: Seq[Byte]) extends Command
  def apply(): Behavior[Store.Command] = {
    Behaviors.setup { context =>
      new Store(context)
    }
  }
}

class Store(context: ActorContext[Store.Command]) extends AbstractBehavior[Store.Command](context) {
  import Store._

  val data = scala.collection.mutable.Map[Seq[Byte], Seq[Byte]]()
  override def onMessage(msg: Store.Command): Behavior[Store.Command] = msg match {
    case Get(replyTo, key) =>
      context.log.info(s"Recieved message to get key: $key")
      replyTo ! Numbers(data(key))
      Behaviors.same
    case Set(replyTo, key, value) =>
      context.log.info(s"Adding values: key: $key, value: $value ")
      data.addOne(key, value)
      replyTo ! Numbers(data(key))
      Behaviors.same
  }
}


