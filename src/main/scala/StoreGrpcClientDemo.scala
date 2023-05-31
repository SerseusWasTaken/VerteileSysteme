import api.StoreGrpcClient
import de.hfu.vs.protocol.{GetRequest, SetRequest}

object StoreGrpcClientDemo extends App {
  val port = 50051
  val host = "localhost"

  val client = StoreGrpcClient(port, host)

  val setResponse = client.set(SetRequest("DE", "Deutschland"))
  println(setResponse)

  val getResponse = client.get(GetRequest("DE"))
  println(getResponse)
}
