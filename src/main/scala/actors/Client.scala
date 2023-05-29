package actors

import actors.Client.{Command, Count, Get, Register, Set, clientServiceKey}
import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import utils.Utils.stringToByteSeq

object Client {
  sealed trait Command extends utils.Serializable

  case class Get(key: String) extends Command

  case class Set(key: String, value: String) extends Command

  case class SetCollectionOfValues(collection: Iterable[(String, String)]) extends Command

  case class Count() extends Command

  case class Register() extends Command

  val clientServiceKey: ServiceKey[Command] = ServiceKey[Command]("client")

  def apply(store: ActorRef[Store.Command]): Behavior[Command] = {
    Behaviors.setup { context =>
      new Client(store, context)
    }
  }
}

class Client(val store: ActorRef[Store.Command], context: ActorContext[Command]) extends AbstractBehavior[Command](context) {
  context.self ! Register()

  override def onMessage(msg: Command): Behavior[Command] = msg match {
    case Get(key: String) =>
      store ! Store.Get(getConsumer, stringToByteSeq(key))
      Behaviors.same
    case Set(key: String, value: String) =>
      store ! Store.Set(getConsumer, stringToByteSeq(key), stringToByteSeq(value))
      Behaviors.same
    case Client.SetCollectionOfValues(collection) =>
      store ! Store.SetCollectionOfValues(getConsumer, collection.map(tuple => (stringToByteSeq(tuple._1), stringToByteSeq(tuple._2))))
      Behaviors.same
    case Count() =>
      store ! Store.Count(getConsumer)
      Behaviors.same
    case Register() =>
      context.system.receptionist ! Receptionist.register(clientServiceKey, context.self)
      Behaviors.same
  }

  private def getConsumer: ActorRef[Consumer.Result] =
    context.spawnAnonymous(Consumer())

}
