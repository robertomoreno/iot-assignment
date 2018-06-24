package iot.assignment.modules

import iot.assignment.repository.AkkaVehicleRepository

class RepositoryModule(val actorModule: ActorModule) {

  import actorModule.akkaModule._

  val vehicleRepository = new AkkaVehicleRepository(actorModule.vehicleShardReaderProxy)

}
