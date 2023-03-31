import akka.actor.typed.ActorSystem

import java.util.Scanner

object Main extends App {
  val system = ActorSystem(Guard(), "HFU")
  val scanner = new Scanner(System.in)
  scanner.next()
}


