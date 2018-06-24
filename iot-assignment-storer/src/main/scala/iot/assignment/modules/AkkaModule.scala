package iot.assignment.modules

import akka.actor.ActorSystem
import akka.cluster.Cluster
import com.typesafe.config.Config

import scala.concurrent.ExecutionContextExecutor
import scala.util.Try

class AkkaModule(val config: ConfigModule) {

  import config._

  private val clusterName = configuration.getString("akka.cluster.name")

  implicit val system : ActorSystem              = ActorSystem(clusterName, configuration)
  implicit val ec     : ExecutionContextExecutor = system.dispatcher
  implicit val cluster: Cluster                  = Cluster(system)


  AkkaModule.logConfiguration(configuration)
}

object AkkaModule {

  private def logConfiguration(config: Config)(implicit system: ActorSystem): Unit = {

    import config._

    val port = getInt("akka.remote.netty.tcp.port")
    val host = getString("akka.remote.netty.tcp.hostname")
    val roles = getStringList("akka.cluster.roles").toArray.mkString(",")
    val seedNodes = getStringList("akka.cluster.seed-nodes").toArray.mkString(",")

    system.log.info("Starting node [{}] in {}:{}", roles, host, port)

    for {
      bindHost <- Try(getString("akka.remote.netty.tcp.bind-hostname")).toOption
      bindPort <- Try(getString("akka.remote.netty.tcp.bind-port")).toOption
    }
      yield
        system.log.info("Starting node under NAT {}:{}", bindHost, bindPort)

    system.log.info("Will try to connect to seed nodes [{}]", seedNodes)
  }
}
