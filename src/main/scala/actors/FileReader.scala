package actors

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
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
      context.log.info(s"Recieved message to read file $filename")
      val readFile = Source.fromFile(filename)
      val lines = readFile.getLines().toList
      readFile.close()
      val separated = lines.map { s =>
        val splitString = s.split(",")
        (splitString(0), splitString(1))
      }
      separated.foreach { value =>
        client ! Client.Set(value._1, value._2)
      }
      Behaviors.same
  }
}


