package api

import io.grpc.ManagedChannelBuilder
import de.hfu.vs.protocol.{GetRequest, GrpcClientGrpc, SetRequest}

object StoreGrpcClient extends App {
  val port = 8080
  val host = "localhost"
  val channel = ManagedChannelBuilder
    .forAddress(host, port)
    .usePlaintext()
    .asInstanceOf[ManagedChannelBuilder[_]]
    .build()

  run()
  def run(): Unit = {
    val setReq = GrpcClientGrpc
      .blockingStub(channel)
      .set(SetRequest("DE", "Germanyy"))
    println(setReq.value)

    val getReq = GrpcClientGrpc
      .blockingStub(channel)
      .get(GetRequest("DE"))

    println(getReq.value)
  }
}
