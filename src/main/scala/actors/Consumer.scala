package actors

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import com.fasterxml.jackson.annotation.JsonTypeName

import java.lang

object Consumer {
  sealed trait Result extends utils.Serializable
  case class ConsumeGet(key: Seq[Byte], value: Seq[Byte]) extends Result
  case class ConsumeSet(key: Seq[Byte], value: Seq[Byte]) extends Result
  case class ConsumeSize(size: Int) extends Result
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
    case ConsumeGet(key: Seq[Byte], value: Seq[Byte]) =>
      context.log.info(s"Set key ${byteSeqToString(key)} to value ${byteSeqToString(value)}")
      Behaviors.stopped
    case ConsumeSet(key: Seq[Byte], value: Seq[Byte]) =>
      context.log.info(s"Value of key ${byteSeqToString(key)} is ${byteSeqToString(value)}")
      Behaviors.stopped
    case ConsumeSize(size: Int) =>
      context.log.info(s"Number of values in store: $size")
      Behaviors.stopped
    case Error(key: Seq[Byte]) =>
      context.log.info(s"Could not find key: ${byteSeqToString(key)}")
      Behaviors.stopped
  }

  private def byteSeqToString(key: Seq[Byte]) = {
    new String (key match {
      case x: Seq[Integer] => x.map(s => s.byteValue()).toArray
      case x: Seq[Byte] => x.toArray
    })
  }
}
