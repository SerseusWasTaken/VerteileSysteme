import actors.{Client, Consumer, FileReader, Store}
import akka.NotUsed
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

object Guard {

  import FileReader.Message

  def apply(): Behavior[NotUsed] = {
    Behaviors.setup { context =>
      /*
      val store = context.spawn(Store(), "store")
      val consumer = context.spawn(Consumer(), "consumer")

      store ! Store.Set(consumer, Seq(1,2), Seq(1,2,3))
      store ! Store.Set(consumer, Seq(1,3), Seq(4,5,6))
      store ! Store.Get(consumer, Seq(1,2))
      store ! Store.Get(consumer, Seq(1,3))


      val client = context.spawn(Client(store), "client")
      client ! Client.Set("IT", "Italia")
      client ! Client.Get("IT")

       */



      val store = context.spawn(Store(), "the-store")
      val client1 = context.spawn(Client(store), "client1")
      val client2 = context.spawn(Client(store), "client2")
      client1 ! Client.Set("IT", "Italia")
      client2 ! Client.Get("IT")
      client1 ! Client.Get("DE")
      client1 ! Client.Get("IT")
      val reader = context.spawn(FileReader(), "reader")
      val filename = "trip_data_1000_000.csv"
      reader ! FileReader.File(filename, client1)

      Behaviors.same
    }
  }
}



