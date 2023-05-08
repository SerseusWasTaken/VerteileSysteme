package utils

import com.typesafe.config.{Config, ConfigFactory}

object Utils {
  def getConfig(port: Int): Config = ConfigFactory
    .parseString(s"akka.remote.artery.canonical.port=$port")
    .withFallback(ConfigFactory.load())

  def byteSeqToString(key: Seq[Byte]): String = {
    new String(key match {
      case x: Seq[Integer] => x.map(s => s.byteValue()).toArray
      case x: Seq[Byte] => x.toArray
    })
  }
}


