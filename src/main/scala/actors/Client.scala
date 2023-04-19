package actors

import actors.Client.{Command, Count, Get, Set}
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}

object Client {
  sealed trait Command extends utils.Serializable

  case class Get(key: String) extends Command

  case class Set(key: String, value: String) extends Command
  case class Count() extends Command

  def apply(store: ActorRef[Store.Command]): Behavior[Command] = {
    Behaviors.setup { context =>
      new Client(store, context)
    }
  }
}

class Client(val store: ActorRef[Store.Command], context: ActorContext[Command]) extends AbstractBehavior[Command](context) {
  override def onMessage(msg: Command): Behavior[Command] = msg match {
    case Get(key: String) =>
      store ! Store.Get(getConsumer, stringToByteSeq(key))
      Behaviors.same
    case Set(key: String, value: String) =>
      store ! Store.Set(getConsumer, stringToByteSeq(key), stringToByteSeq(value))
      Behaviors.same
    case Count() =>
      store ! Store.Count(getConsumer)
      Behaviors.same
  }

  private def getConsumer: ActorRef[Consumer.Result] =
    context.spawnAnonymous(Consumer())

  private def stringToByteSeq(string: String) = string.toSeq.map { s => s.toByte }
}
