package actors

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}

import java.io
import java.io.BufferedReader
import java.util.Scanner
import scala.io.Source

object FileReader {
  sealed trait Message

  case class File(filename: String, client: ActorRef[Client.Command]) extends Message

  def apply(): Behavior[FileReader.Message] = {
    Behaviors.setup { context =>
      new FileReader(context)
    }
  }
}

class FileReader(context: ActorContext[FileReader.Message]) extends AbstractBehavior[FileReader.Message](context) {

  import FileReader._

  override def onMessage(msg: FileReader.Message): Behavior[FileReader.Message] = msg match {
    case File(filename, client) =>
      val scanner = new Scanner(new io.FileReader(filename))
      while(scanner.hasNext()) {
        val sep = scanner.nextLine().split(",")
        client ! Client.Set(sep(0), sep(1))
      }
      scanner.close()
      Behaviors.same
  }
}


