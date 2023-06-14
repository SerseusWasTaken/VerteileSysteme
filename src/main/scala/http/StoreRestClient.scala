package http

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.{Http, HttpExt}
import utils.{KeyValuePair, StoreClient}
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

import scala.util.{Failure, Success}

class StoreRestClient(endpoint: String, system: ActorSystem[_]) extends StoreClient {
  implicit val actorSystem: ActorSystem[_] = system
  implicit val executionContext = system.executionContext

  val http: HttpExt = Http()

  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  override def get(key: String, action: Option[String] => Unit): Unit = {
    val res = http.singleRequest(HttpRequest(uri = s"$endpoint/$key"))
    res.onComplete {
      case Failure(exception) =>
        action(Option(null))
      case Success(value) =>
        val body = Unmarshal(value.entity).to[String].value.get.get //Unsafe retrieval of body. Future should always be fulfilled though
        try {
          val result = KeyValuePair.deserialize(body)
          action(Option(result.value))
        } catch {
          case e: JsonParseException => action(Option(null))
        }
    }
  }

  override def set(key: String, value: String): Unit = {
    val keyValuePair = KeyValuePair(key, value)
    http.singleRequest(
      HttpRequest(
        uri = endpoint,
        method = HttpMethods.POST,
        entity = HttpEntity(ContentTypes.`application/json`, keyValuePair.serialize())
      )
    )
  }
}

object StoreRestClient {
  def apply(endpoint: String, system: ActorSystem[_]): StoreRestClient = {
    new StoreRestClient(endpoint, system)
  }
}
