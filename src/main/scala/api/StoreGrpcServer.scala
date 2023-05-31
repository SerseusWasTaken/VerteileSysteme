package api

import de.hfu.vs.protocol.GrpcClientGrpc.GrpcClient
import io.grpc.ServerBuilder

import scala.concurrent.ExecutionContext

object StoreGrpcServer extends {
  def runService(service: GrpcClient): Unit = {
    val port = 50051
    val host = "localhost"
    val defintion = GrpcClient.bindService(service, ExecutionContext.global)
    val server = ServerBuilder
      .forPort(port)
      .addService(defintion)
      .asInstanceOf[ServerBuilder[_]]
      .build()
      .start()
  }
}
