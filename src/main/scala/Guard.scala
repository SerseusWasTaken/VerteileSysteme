import actors.StoreShard.TypeKey
import actors.{Client, FileReader, Store, StoreShard}
import akka.actor.typed.Behavior
import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.Behaviors
import akka.cluster.sharding.typed.scaladsl.{ClusterSharding, Entity}

object Guard {

  def apply(): Behavior[Nothing] = {
    Behaviors.setup[Receptionist.Listing] { context =>

      val sharding = ClusterSharding(context.system)
      sharding.init(Entity(TypeKey) { entityContext =>
        StoreShard(entityContext.entityId)
      }.withRole("storeShard"))

      context.system.settings.config.getInt("akka.remote.artery.canonical.port") match {
        case 25251 =>
          context.spawnAnonymous(Store(sharding, 5))
        case 25252 =>
          context.system.receptionist ! Receptionist.Subscribe(Client.clientServiceKey, context.self)
        case 25253 =>
          context.system.receptionist ! Receptionist.Subscribe(Store.storeServiceKey, context.self)
      }

      Behaviors.receiveMessagePartial[Receptionist.Listing] {
        case Store.storeServiceKey.Listing(listings) =>
          listings.foreach { store =>
            context.spawnAnonymous(Client(store))
          }
          Behaviors.same
        case Client.clientServiceKey.Listing(listings) =>
          listings.foreach { client =>
            val fileReader = context.spawnAnonymous(FileReader())
            fileReader ! FileReader.File("trip_data_1000_000.csv", 10, client)
          }
          Behaviors.same
      }
    }
  }.narrow
}



