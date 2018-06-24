package iot.assignment.modules

import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.config.ConfigValueFactory.fromIterable

import scala.collection.JavaConverters._

class ConfigModule {

  private val config = ConfigFactory.load

  private val seedNodes = getSeedNodes()

  val configuration: Config =
    if (seedNodes.isEmpty)
      config
    else
      ConfigFactory.empty
        .withValue("akka.cluster.seed-nodes", fromIterable(seedNodes.asJava))
        .withFallback(config)

  private def getSeedNodes(): Set[String] = {
    val seedNodes = sys.env.get("AKKA_SEEDNODES")
    seedNodes.map(_.split(",")).toSet.flatten
  }

}
