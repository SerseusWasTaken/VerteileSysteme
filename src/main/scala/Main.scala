import akka.actor.typed.ActorSystem

import java.util.Scanner

object Main extends App {
  val system = ActorSystem[Nothing](Guard(), "HFU")
  val scanner = new Scanner(System.in)
  scanner.next()
}


