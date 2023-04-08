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


      implicit val timeout: Timeout = Timeout.apply(100, TimeUnit.MILLISECONDS)

      context.ask(
        context.system.receptionist,
        Receptionist.Find(Store.storeServiceKey)
      ) {
        case Success(value) =>
          val store = value.serviceInstances(Store.storeServiceKey).head
          val client1 = context.spawn(Client(store), "client1")
          val client2 = context.spawn(Client(store), "client2")
          client1 ! Client.Set("IT", "Italia")
          client2 ! Client.Get("IT")
          client1 ! Client.Get("DE")
          client1 ! Client.Get("IT")
          value
      }

      /*
      val store = context.spawn(Store(), "the-store")
      val client1 = context.spawn(Client(store), "client1")
      val client2 = context.spawn(Client(store), "client2")
      client1 ! Client.Set("IT", "Italia")
      client2 ! Client.Get("IT")
      client1 ! Client.Get("DE")
      client1 ! Client.Get("IT")
      val reader = context.spawn(FileReader(5), "reader")
      val filename = "trip_data_1000_000.csv"
      reader ! FileReader.File(filename, client1)

       */

      Behaviors.same
    }
  }.narrow
}



