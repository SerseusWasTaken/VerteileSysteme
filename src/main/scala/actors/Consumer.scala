package actors

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}

object Consumer {
  sealed trait Result
  case class Numbers(msg: Seq[Byte]) extends Result
  def apply(): Behavior[Consumer.Result] = {
    Behaviors.setup { context =>
      new Consumer(context)
    }
  }
}
class Consumer(context: ActorContext[Consumer.Result]) extends AbstractBehavior[Consumer.Result](context){
  import Consumer._
  override def onMessage(msg: Consumer.Result): Behavior[Consumer.Result] = msg match {
    case Numbers(msg) =>
      context.log.info(s"Recieved numbers: $msg")
      Behaviors.same
  }
}
