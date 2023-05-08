package actors

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import utils.Utils

object Consumer {
  sealed trait Result extends utils.Serializable
  case class ConsumeGet(key: Seq[Byte], value: Seq[Byte]) extends Result
  case class ConsumeSet(key: Seq[Byte], value: Seq[Byte]) extends Result
  case class ConsumeGroupSet(collection: Iterable[(Seq[Byte], Seq[Byte])]) extends Result
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
      context.log.info(s"Value of key ${Utils.byteSeqToString(key)} is ${Utils.byteSeqToString(value)}")
      Behaviors.stopped
    case ConsumeSet(key: Seq[Byte], value: Seq[Byte]) =>
      context.log.info(s"Set key ${Utils.byteSeqToString(key)} to value ${Utils.byteSeqToString(value)}")
      Behaviors.stopped
    case ConsumeGroupSet(collection) =>
      context.log.info(s"Set key/value pairs: ${collection.map(pair => {(Utils.byteSeqToString(pair._1), Utils.byteSeqToString(pair._2))})}")
      Behaviors.stopped
    case ConsumeSize(size: Int) =>
      context.log.info(s"Number of values in store: $size")
      Behaviors.stopped
    case Error(key: Seq[Byte]) =>
      context.log.info(s"Could not find key: ${Utils.byteSeqToString(key)}")
      Behaviors.stopped
  }

}
