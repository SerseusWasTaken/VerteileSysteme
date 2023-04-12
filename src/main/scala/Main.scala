import akka.actor.typed.ActorSystem
import utils.Utils

import java.util.Scanner

object Main {
  def main(args: Array[String]): Unit = {
    val config = Utils.getConfig(args(0).toInt)
    val system = ActorSystem[Nothing](Guard(), "hfu", config)
    val scanner = new Scanner(System.in)
    scanner.next()
  }

}


