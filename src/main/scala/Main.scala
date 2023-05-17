import akka.actor.typed.ActorSystem
import utils.Utils

import java.util.Scanner

object Main {
  def main(args: Array[String]): Unit = {
    //runWithPort(args(0).toInt)
    runOnSameJVM()

    val scanner = new Scanner(System.in)
    scanner.next()
  }

  private def runWithPort(port: Int) = {
    val config = Utils.getConfig(port)
    ActorSystem[Nothing](Guard(), "hfu", config)
  }

  private def runOnSameJVM(): Unit = {

    val config = Utils.getConfig(25251)
    val system = ActorSystem[Nothing](Guard(), "hfu", config)

    val config2 = Utils.getConfig(25252)
    val system2 = ActorSystem[Nothing](Guard(), "hfu", config2)

    val config3 = Utils.getConfig(25253)
    val system3 = ActorSystem[Nothing](Guard(), "hfu", config3)
  }

}


