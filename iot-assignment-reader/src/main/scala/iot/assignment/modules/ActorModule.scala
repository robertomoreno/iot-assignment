package iot.assignment.modules

import akka.actor.ActorRef
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import iot.assignment.actor.{VehicleActorReader, VehicleReaderShard}

class ActorModule(val akkaModule: AkkaModule) {

  import akkaModule._

  implicit val clusterSharding: ClusterSharding = ClusterSharding(system)

  val vehicleShard: ActorRef = clusterSharding.start(
    typeName = VehicleReaderShard.shardName,
    entityProps = VehicleActorReader.props(),
    settings = ClusterShardingSettings(system),
    extractEntityId = VehicleReaderShard.idExtractor,
    extractShardId = VehicleReaderShard.shardResolver
  )
}
