package actors

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import Consumer.{ConsumeGet, ConsumeSet, ConsumeSize, Error, Result}
import actors.Store.Register
import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import Store._

object Store {
  sealed trait Command extends utils.Serializable

  case class Get(replyTo: ActorRef[Result], key: Seq[Byte]) extends Command

  case class Set(replyTo: ActorRef[Result], key: Seq[Byte], value: Seq[Byte]) extends Command
  case class Count(replyTo: ActorRef[Result]) extends Command
  case class Register() extends Command

  val storeServiceKey: ServiceKey[Command] = ServiceKey[Command]("store")
  def apply(): Behavior[Store.Command] = {
    Behaviors.setup { context =>
      new Store(context)
    }
  }
}

class Store(context: ActorContext[Store.Command]) extends AbstractBehavior[Store.Command](context) {
  context.self ! Register()
  private val data = scala.collection.mutable.Map[Seq[Byte], Seq[Byte]]()

  override def onMessage(msg: Store.Command): Behavior[Store.Command] = msg match {
    case Get(replyTo, key) =>
      val value = data.get(key)
      if (value.isEmpty) {
        replyTo ! Error(key)
      } else {
        replyTo ! ConsumeGet(key, value.get)
      }
      Behaviors.same
    case Set(replyTo, key, value) =>
      data.addOne(key, value)
      replyTo ! ConsumeSet(key, data(key))
      Behaviors.same
    case Count(replyTo) =>
      replyTo ! ConsumeSize(data.size)
      Behaviors.same
    case Register() =>
      context.system.receptionist ! Receptionist.register(storeServiceKey, context.self)
      Behaviors.same
  }
}


