package actors

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import Consumer.Result
import actors.Store.Register
import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import Store._
import akka.cluster.sharding.typed.scaladsl.ClusterSharding
import akka.util.Timeout
import utils.Utils

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration.DurationInt


object Store {
  sealed trait Command extends utils.Serializable

  case class Get(replyTo: ActorRef[Result], key: Seq[Byte]) extends Command

  case class Set(replyTo: ActorRef[Result], key: Seq[Byte], value: Seq[Byte]) extends Command

  case class SetCollectionOfValues(replyTo: ActorRef[Result], collection: Iterable[(Seq[Byte], Seq[Byte])]) extends Command

  case class GetCollectionOfValues(replyTo: ActorRef[Result], keys: Iterable[Seq[Byte]]) extends Command

  case class Count(replyTo: ActorRef[Result]) extends Command

  case class Register() extends Command

  val storeServiceKey: ServiceKey[Command] = ServiceKey[Command]("store")

  def apply(sharding: ClusterSharding, numberOfEntities: Int): Behavior[Store.Command] = {
    Behaviors.setup { context =>
      new Store(context, sharding, numberOfEntities)
    }
  }
}

class Store(context: ActorContext[Store.Command], sharding: ClusterSharding, numberOfEntities: Int) extends AbstractBehavior[Store.Command](context) {
  context.self ! Register()

  override def onMessage(msg: Store.Command): Behavior[Store.Command] = msg match {
    case Get(replyTo: ActorRef[Result], key: Seq[Byte]) =>
      getStoreShard(key) ! StoreShard.Get(replyTo, key)
      Behaviors.same
    case Set(replyTo: ActorRef[Result], key: Seq[Byte], value: Seq[Byte]) =>
      getStoreShard(key) ! StoreShard.Set(replyTo, key, value)
      Behaviors.same
    case SetCollectionOfValues(replyTo, collection) =>
      collection.foreach(tuple => getStoreShard(tuple._1) ! StoreShard.Set(replyTo, tuple._1, tuple._2))
      Behaviors.same
    case GetCollectionOfValues(replyTo, keys) =>
      keys.foreach(key => getStoreShard(key) ! StoreShard.Get(replyTo, key))
      Behaviors.same
    case Count(replyTo: ActorRef[Result]) =>
      implicit val timeout: Timeout = 5000.millis
      val refs = Range(0, numberOfEntities).map(i => sharding.entityRefFor(StoreShard.TypeKey, s"Shard$i"))
      val results = refs.map(entity => Await.result(entity.ask(StoreShard.Count), 5000.millis)) //Blocking await
      val sizes = results.map(result => result.asInstanceOf[Consumer.ConsumeSize].size)
      replyTo ! Consumer.ConsumeSize(sizes.sum)
      Behaviors.same
    case Register() =>
      context.system.receptionist ! Receptionist.register(storeServiceKey, context.self)
      Behaviors.same
  }

  private def getStoreShard(key: Seq[Byte]) = {
    val shardId = Math.floorMod(Utils.byteSeqToString(key).hashCode, numberOfEntities)
    sharding.entityRefFor(StoreShard.TypeKey, s"Shard$shardId")
  }
}