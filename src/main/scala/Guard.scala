import actors.{Client, FileReader, Store, StoreShard}
import akka.actor.typed.Behavior
import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.Behaviors
import akka.cluster.sharding.typed.scaladsl.ClusterSharding

object Guard {

  def apply(): Behavior[Nothing] = {
    Behaviors.setup[Receptionist.Listing] { context =>

      context.system.settings.config.getInt("akka.remote.artery.canonical.port") match {
        case 25251 =>
          context.spawnAnonymous(Store())
        case 25252 =>
          context.system.receptionist ! Receptionist.Subscribe(Client.clientServiceKey, context.self)
        case 25253 =>
          context.system.receptionist ! Receptionist.Subscribe(Store.storeServiceKey, context.self)
      }
      val sharding = ClusterSharding(context.system)
      val ref = sharding.entityRefFor(StoreShard.TypeKey, 1L)

      Behaviors.receiveMessagePartial[Receptionist.Listing] {
        case Store.storeServiceKey.Listing(listings) =>
          listings.foreach { store =>
            context.spawnAnonymous(Client(store))
          }
          Behaviors.same
        case Client.clientServiceKey.Listing(listings) =>
          listings.foreach { client =>
            client ! Client.Set("IT", "Italia")
            client ! Client.Get("IT")
            client ! Client.Get("DE")
            client ! Client.Get("IT")
            client ! Client.Count()
            val fileReader = context.spawnAnonymous(FileReader(5))
            //fileReader ! FileReader.File("trip_data_1000_000.csv", client)
          }
          Behaviors.same
      }




    }
  }.narrow
}



