package iot.assignment.modules

import akka.actor.ActorRef
import akka.cluster.sharding.ClusterSharding
import iot.assignment.actor.VehicleWriterShard

class ActorModule(val akkaModule: AkkaModule) {

  import akkaModule._

  implicit val clusterSharding: ClusterSharding = ClusterSharding(system)

  val vehicleShardProxy: ActorRef =
    clusterSharding.startProxy(
      typeName = VehicleWriterShard.shardName,
      role = Some("storer"),
      extractEntityId = VehicleWriterShard.idExtractor,
      extractShardId = VehicleWriterShard.shardResolver)
}
