import actors.Store
import akka.NotUsed
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

object Guard {
  def apply: Behavior[NotUsed] = Behaviors.setup { context =>
    val store = context.spawn(Store(context), "")
  }
}
