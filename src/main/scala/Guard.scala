import actors.{Client, FileReader, Store}
import akka.actor.typed.Behavior
import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.Behaviors
import akka.util.Timeout

import scala.util.{Failure, Success}
import java.util.concurrent.TimeUnit

object Guard {

  def apply(): Behavior[Nothing] = {
    Behaviors.setup[Receptionist.Listing] { context =>
      context.spawnAnonymous(Store())
      Behaviors.same

      context.system.receptionist ! Receptionist.Subscribe(Store.storeServiceKey, context.self)
      Behaviors.receiveMessagePartial[Receptionist.Listing] {
        case Store.storeServiceKey.Listing(listings) =>
          listings.foreach { ps =>
            val client1 = context.spawnAnonymous(Client(ps))
            val client2 = context.spawnAnonymous(Client(ps))
            client1 ! Client.Set("IT", "Italia")
            client2 ! Client.Get("IT")
            client1 ! Client.Get("DE")
            client1 ! Client.Get("IT")
            val fileReader = context.spawnAnonymous(FileReader(5))
            fileReader ! FileReader.File("small.csv", client1)
          }
          Behaviors.same
      }
    }
  }.narrow
}



