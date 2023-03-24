package actors

import akka.NotUsed
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.util.LineNumbers.Result

object Store {
  sealed trait Command
  case class Get(replyTo: ActorRef[Result], key: Seq[Byte]) extends Command
  case class Set(replyTo: ActorRef[Result], key: Seq[Byte], value: Seq[Byte]) extends Command

  def apply(context: ActorContext[Store.Command]) = new Store(context)

}
class Store(context: ActorContext[Store.Command]) extends AbstractBehavior[Store.Command](context) {
  override def onMessage(msg: Store.Command): Behavior[Store.Command] = ???
}
