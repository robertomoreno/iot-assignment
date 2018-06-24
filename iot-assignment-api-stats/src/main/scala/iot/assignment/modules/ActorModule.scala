package iot.assignment.modules

import akka.actor.ActorRef
import akka.cluster.sharding.ClusterSharding
import iot.assignment.actor.VehicleReaderShard

class ActorModule(val akkaModule: AkkaModule) {

  import akkaModule._

  implicit val clusterSharding: ClusterSharding = ClusterSharding(system)

  val vehicleShardReaderProxy: ActorRef =
    clusterSharding.startProxy(
      typeName = VehicleReaderShard.shardName,
      role = Some("reader"),
      extractEntityId = VehicleReaderShard.idExtractor,
      extractShardId = VehicleReaderShard.shardResolver)

}
