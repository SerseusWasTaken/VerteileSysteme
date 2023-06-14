package api

import de.hfu.vs.protocol.{GetRequest, GrpcClientGrpc, SetRequest}
import io.grpc.ManagedChannelBuilder
import utils.StoreClient

class StoreGrpcClient(port: Int, host: String) extends StoreClient {
  val channel = ManagedChannelBuilder
    .forAddress(host, port)
    .usePlaintext()
    .asInstanceOf[ManagedChannelBuilder[_]]
    .build()

  private def doGet(req: GetRequest) =
    GrpcClientGrpc.blockingStub(channel)
      .get(req)

  private def doSet(request: SetRequest) =
    GrpcClientGrpc.blockingStub(channel)
      .set(request)

  override def get(key: String, action: Option[String] => Unit): Unit = {
    val req = GetRequest(key)
    val response = doGet(req)
    action(response.value)
  }

  override def set(key: String, value: String): Unit = {
    val req = SetRequest(key, value)
    val response = doSet(req)
  }
}

object StoreGrpcClient {
  def apply(port: Int, host: String) = new StoreGrpcClient(port, host)
}
