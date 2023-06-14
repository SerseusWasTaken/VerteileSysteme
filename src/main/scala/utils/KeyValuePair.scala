package utils

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import spray.json.DefaultJsonProtocol.{StringJsonFormat, jsonFormat2}
import spray.json.RootJsonFormat
import utils.KeyValuePair.mapper

final case class KeyValuePair(@JsonProperty key: String, @JsonProperty value: String) {
  def serialize(): String = mapper.writeValueAsString(this)
}

object KeyValuePair{
  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)
  implicit val keyValuePairFormat: RootJsonFormat[KeyValuePair] = jsonFormat2(KeyValuePair.apply)
  def deserialize(json: String): KeyValuePair = mapper.readValue(json, classOf[KeyValuePair])
}

