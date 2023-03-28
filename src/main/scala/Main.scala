import akka.actor.typed.ActorSystem

object Main extends App {
  val system = ActorSystem(Guard(), "Name")
}


