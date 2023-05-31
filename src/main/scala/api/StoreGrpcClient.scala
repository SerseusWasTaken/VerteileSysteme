package api

import de.hfu.vs.protocol.{GetRequest, GrpcClientGrpc, SetRequest}
import io.grpc.ManagedChannelBuilder

class StoreGrpcClient(port: Int, host: String) {
  val channel = ManagedChannelBuilder
    .forAddress(host, port)
    .usePlaintext()
    .asInstanceOf[ManagedChannelBuilder[_]]
    .build()

  def get(req: GetRequest) =
    GrpcClientGrpc.blockingStub(channel)
      .get(req)

  def set(request: SetRequest) =
    GrpcClientGrpc.blockingStub(channel)
      .set(request)
}

object StoreGrpcClient {
  def apply(port: Int, host: String) = new StoreGrpcClient(port, host)
}
