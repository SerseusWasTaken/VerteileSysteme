package actors

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}

object Consumer {
  sealed trait Result
  case class ByteSeq(msg: Seq[Byte]) extends Result
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
    case ByteSeq(msg) =>
      val result = new String(msg.toArray)
      context.log.info(s"Recieved value to print: $result")
      Behaviors.same
    case Error(key) =>
      val result = new String(key.toArray)
      context.log.info(s"Could not find key: $result")
      Behaviors.same
  }
}
