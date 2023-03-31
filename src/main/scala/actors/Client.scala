package actors

import actors.Client.{Command, Get, Set}
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}

object Client {
  sealed trait Command

  case class Get(key: String) extends Command

  case class Set(key: String, value: String) extends Command

  def apply(consumer: ActorRef[Store.Command]): Behavior[Command] = {
    Behaviors.setup { context =>
      new Client(consumer, context)
    }
  }
}

class Client(val store: ActorRef[Store.Command], context: ActorContext[Command]) extends AbstractBehavior[Command](context) {
  override def onMessage(msg: Command): Behavior[Command] = msg match {
    case Get(key) =>
      context.log.info(s"Recieved command to get key: $key")
      val consumer = context.spawnAnonymous(Consumer())
      store ! Store.Get(consumer, stringToByteSeq(key))
      Behaviors.same
    case Set(key, value) =>
      context.log.info(s"Recieved command to add values key: $key and value: $value")
      val consumer = context.spawnAnonymous(Consumer())
      store ! Store.Set(consumer, stringToByteSeq(key), stringToByteSeq(value))
      Behaviors.same
  }

  private def stringToByteSeq(string: String) = string.toSeq.map { s => s.toByte }
}
