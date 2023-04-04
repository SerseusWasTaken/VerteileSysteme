package actors

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}

object Consumer {
  sealed trait Result
  case class ConsumeGet(key: Seq[Byte], value: Seq[Byte]) extends Result
  case class ConsumeSet(key: Seq[Byte], value: Seq[Byte]) extends Result
  case class Error(key: Seq[Byte]) extends Result
  def apply(): Behavior[Consumer.Result] = {
    Behaviors.setup { context =>
      new Consumer(context)
    }
  }
}
class Consumer(context: ActorContext[Consumer.Result]) extends AbstractBehavior[Consumer.Result](context){
  import Consumer._
  override def onMessage(msg: Consumer.Result): Behavior[Consumer.Result] = msg match {
    case ConsumeGet(key, value) =>
      val keyAsString = new String(key.toArray)
      val valueAsString = new String(value.toArray)
      context.log.info(s"Set key $keyAsString to value $valueAsString")
      Behaviors.same
    case ConsumeSet(key, value) =>
      val keyAsString = new String(key.toArray)
      val valueAsString = new String(value.toArray)
      context.log.info(s"Value of key $keyAsString is $valueAsString")
      Behaviors.same
    case Error(key) =>
      val result = new String(key.toArray)
      context.log.info(s"Could not find key: $result")
      Behaviors.same
  }
}
