package api

import de.hfu.vs.protocol.GrpcClientGrpc.GrpcClient
import io.grpc.ServerBuilder

import scala.concurrent.ExecutionContext

object StoreGrpcServer extends {
  def runService(service: GrpcClient): Unit = {
    val port = 8080
    val host = "localhost"
    val service = GrpcClient.bindService(service, ExecutionContext.global)
    val server = ServerBuilder
      .forPort(port)
      .addService(service)
      .asInstanceOf[ServerBuilder[_]]
      .build()
      .start()

    server.awaitTermination()
  }
}
