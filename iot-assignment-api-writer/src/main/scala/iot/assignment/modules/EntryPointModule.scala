package iot.assignment.modules

import iot.assignment.entrypoint.VehicleWriterApi

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success}

class EntryPointModule(shard: ActorModule) {

  import shard.akkaModule._

  val api = new VehicleWriterApi(shard.vehicleShardProxy)

  private val serverPort = config.configuration.getInt("server.http.port")
  private val serverHost = config.configuration.getString("server.http.host")

  api.run(serverPort,serverHost).onComplete {
    case Success(v) ⇒
      system.log.info("Successfully started http server! ({})",v)

    case Failure(ex) ⇒

      system.log.error(s"Failed to start http server, triggering shutdown! ({})",ex)

      cluster.leave(cluster.selfAddress)
      system.registerOnTermination(System exit -1)
      system.scheduler.scheduleOnce(10 seconds)(System exit -1)
      system.terminate()
  }
}
