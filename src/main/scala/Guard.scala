import actors.{Consumer, Store}
import akka.NotUsed
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

object Guard {
  import Store.Command
  def apply(): Behavior[NotUsed] = {
    Behaviors.setup{ context =>
      val store = context.spawn(Store(), "store")
      val consumer = context.spawn(Consumer(), "consumer")
      store ! Store.Set(consumer, Seq(1,2), Seq(1,2,3))
      store ! Store.Set(consumer, Seq(1,2), Seq(1,2,3))
      store ! Store.Get(consumer, Seq(1,2))
      Behaviors.same
    }
  }
}



