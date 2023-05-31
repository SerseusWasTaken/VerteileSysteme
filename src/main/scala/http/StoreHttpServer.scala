package http


import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

object StoreHttpServer {

  def start(routes: Route, port: Int, system: ActorSystem[_]): Unit = {
    implicit val system = ActorSystem(Behaviors.empty, "my-system")
    implicit val executionContext = system.executionContext


    val bindingFuture = Http().newServerAt("127.0.0.1", port).bind(routes)

    system.log.info(s"Server now online")
  }
}
