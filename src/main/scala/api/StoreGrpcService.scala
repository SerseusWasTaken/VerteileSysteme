package api

import actors.{Consumer, Store}
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.actor.typed.scaladsl.ActorContext
import de.hfu.vs.protocol.GrpcClientGrpc.GrpcClient
import de.hfu.vs.protocol.{GetReply, GetRequest, SetReply, SetRequest}
import utils.Utils

import scala.concurrent.{ExecutionContextExecutor, Future}
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout

import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

class StoreGrpcService(store: ActorRef[Store.Command], context: ActorContext[_]) extends GrpcClient {
  implicit val timeout: Timeout = 5000.millis
  implicit val executionContext: ExecutionContextExecutor = context.system.executionContext
  implicit val system: ActorSystem[_] = context.system

  override def set(request: SetRequest): Future[SetReply] =
    store.ask(Store.Set(_, Utils.stringToByteSeq(request.key), Utils.stringToByteSeq(request.value))).transform {
      case Failure(exception) =>
        Failure(exception)
      case Success(value) =>
        Success(value match {
          case Consumer.ConsumeSet(key, value) => SetReply(Utils.byteSeqToString(key), Utils.byteSeqToString(value))
          case Consumer.Error(key) => SetReply(Utils.byteSeqToString(key), null)
        })

    }

  override def get(request: GetRequest): Future[GetReply] =
    store.ask(Store.Get(_, Utils.stringToByteSeq(request.key))).transform {
      case Failure(exception) =>
        Failure(exception)
      case Success(value) =>
        Success(
          value match {
            case Consumer.ConsumeGet(key, value) => GetReply(Utils.byteSeqToString(key), Option(Utils.byteSeqToString(value)))
            case Consumer.Error(key) => GetReply(Utils.byteSeqToString(key), Option(null))
          }
        )

    }
}

