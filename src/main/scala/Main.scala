import akka.actor.typed.{ActorSystem, Behavior}
import actors.Store
import actors.Store.Get
import akka.NotUsed
import akka.actor.typed.scaladsl.Behaviors

object Main extends App {
  val system = ActorSystem(Guard(), "Name")
}


