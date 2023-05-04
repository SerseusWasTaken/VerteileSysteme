package actors

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.japi.Pair

import java.io
import java.io.BufferedReader
import java.util.Scanner
import scala.io.Source
import scala.util.Using

object FileReader {
  sealed trait Message extends utils.Serializable

  case class File(filename: String, client: ActorRef[Client.Command]) extends Message

  def apply(batchSize: Int): Behavior[FileReader.Message] = {
    Behaviors.setup { context =>
      new FileReader(batchSize, context)
    }
  }
}

class FileReader(batchSize: Int, context: ActorContext[FileReader.Message]) extends AbstractBehavior[FileReader.Message](context) {

  import FileReader._

  override def onMessage(msg: FileReader.Message): Behavior[FileReader.Message] = msg match {
    case File(filename: String, client: ActorRef[Client.Command]) =>
      Using(Source.fromFile(filename)) { source =>
        source.getLines().map(line => {
          val actualLine = line.split(",")
          (actualLine(0), actualLine(1))
        }).grouped(batchSize).foreach(group => {
          client ! Client.SetCollectionOfValues(group)
        })
      }
      Behaviors.same
  }

}
