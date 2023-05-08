package actors

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import scala.io.Source
import scala.util.Using

object FileReader {
  sealed trait Message extends utils.Serializable

  case class File(filename: String,numberOfLines: Int, client: ActorRef[Client.Command]) extends Message
  case class FileBatched(filename: String, batchSize: Int, client: ActorRef[Client.Command]) extends Message

  def apply(): Behavior[FileReader.Message] = {
    Behaviors.setup { context =>
      new FileReader(context)
    }
  }
}

class FileReader(context: ActorContext[FileReader.Message]) extends AbstractBehavior[FileReader.Message](context) {

  import FileReader._

  override def onMessage(msg: FileReader.Message): Behavior[FileReader.Message] = msg match {
    case FileBatched(filename, batchSize, client) =>
      Using(Source.fromFile(filename)) { source =>
        source.getLines().map(line => {
          val actualLine = line.split(",")
          (actualLine(0), actualLine(1))
        }).grouped(batchSize).foreach(group => {
          client ! Client.SetCollectionOfValues(group)
        })
      }
      Behaviors.same
    case File(filename: String, numberOfLines: Int, client: ActorRef[Client.Command]) =>
      Using(Source.fromFile(filename)) { source =>
        source.getLines().map(line => {
          val actualLine = line.split(",")
          (actualLine(0), actualLine(1))
        }).take(numberOfLines).foreach(tuple => {
          client ! Client.Set(tuple._1, tuple._2)
        })
      }
      Behaviors.same
  }

}
