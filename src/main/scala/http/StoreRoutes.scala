package http

import actors.{Consumer, Store}
import akka.actor.typed.{ActorRef, ActorSystem, Scheduler}
import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.http.scaladsl.server.{PathMatchers, Route}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes

import scala.util.{Failure, Success}
import akka.util.Timeout
import utils.Utils
import scala.concurrent.duration.DurationInt
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

import scala.concurrent.ExecutionContext

class StoreRoutes(system: ActorSystem[_], store: ActorRef[Store.Command]) {
  implicit val executionContext: ExecutionContext = system.executionContext

  implicit val timeout: Timeout = 5000.millis
  implicit val context: ActorSystem[_] = system
  implicit val scheduler: Scheduler = system.scheduler

  final case class KeyValuePair(key: String, value: String)

  implicit val keyValuePairFormat: RootJsonFormat[KeyValuePair] = jsonFormat2(KeyValuePair.apply)

  private def query(key: String) = store.ask(Store.Get(_, Utils.stringToByteSeq(key))).transform {
    case Failure(exception) =>
      Failure(exception)
    case Success(value) =>
      Success(
        value match {
          case Consumer.ConsumeGet(key, value) => Option(KeyValuePair(Utils.byteSeqToString(key), Utils.byteSeqToString(value)))
          case Consumer.Error(key) => Option(null)
        }
      )
  }

  private def set(keyValuePair: KeyValuePair) =
    store.ask(Store.Set(_, Utils.stringToByteSeq(keyValuePair.key), Utils.stringToByteSeq(keyValuePair.value))).transform {
      case Failure(exception) => Failure(exception)
      case Success(value) =>
        Success(value match {
          case Consumer.ConsumeSet(key, value) => "key-value-pair created"
          case Consumer.Error(key) => "Error while setting value"
        })
    }

  val route: Route = {
    concat(
      get {
        pathPrefix("store" / PathMatchers.Segment) { id =>
          val maybeItem = query(id)

          onSuccess(maybeItem) {
            case Some(item) => complete(item)
            case None => complete(StatusCodes.NotFound)
          }
        }
      },
      post {
        path("store") {
          entity(as[KeyValuePair]) { keyValuePair =>
            val saved = set(keyValuePair)
            onSuccess(saved) { result =>
              complete(result)
            }
          }
        }
      }
    )
  }
}