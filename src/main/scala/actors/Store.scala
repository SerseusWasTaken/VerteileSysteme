package actors

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import Consumer.{ByteSeq, Result}

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

  private val data = scala.collection.mutable.Map[Seq[Byte], Seq[Byte]]()

  override def onMessage(msg: Store.Command): Behavior[Store.Command] = msg match {
    case Get(replyTo, key) =>
      val keyAsString = new String(key.toArray)
      context.log.info(s"Received message to get key: $keyAsString")
      val value = data.get(key)
      if (value.isEmpty) {
        context.log.info(s"Value of key $keyAsString not found")
        Behaviors.same
      } else {
        replyTo ! ByteSeq(value.get)
        Behaviors.same
      }
    case Set(replyTo, key, value) =>
      val keyAsString = new String(key.toArray)
      val valueAsString = new String(value.toArray)
      context.log.info(s"Received message to set values: key: $keyAsString, value: $valueAsString")
      data.addOne(key, value)
      replyTo ! ByteSeq(data(key))
      Behaviors.same
  }
}


