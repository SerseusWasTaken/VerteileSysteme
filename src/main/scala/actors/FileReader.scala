package actors

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import scala.io.Source
import scala.util.Using

object FileReader {
  sealed trait Message extends utils.Serializable

  case class FileBatched(filename: String, batchSize: Int, numberOfLines: Int, client: ActorRef[Client.Command]) extends Message

  def apply(): Behavior[FileReader.Message] = {
    Behaviors.setup { context =>
      new FileReader(context)
    }
  }
}

class FileReader(context: ActorContext[FileReader.Message]) extends AbstractBehavior[FileReader.Message](context) {

  import FileReader._

  override def onMessage(msg: FileReader.Message): Behavior[FileReader.Message] = msg match {
    case FileBatched(filename, batchSize, numberOfLines: Int, client) =>
      Using(Source.fromFile(filename)) { source =>
        source.getLines().map(line => {
          val actualLine = line.split(",")
          (actualLine(0), actualLine(1))
        }).grouped(batchSize).take(numberOfLines).foreach(group => {
          client ! Client.SetCollectionOfValues(group)
        })
      }
      Behaviors.same
  }
}
