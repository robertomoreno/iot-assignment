package iot.assignment.modules

import akka.actor.ActorRef
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import iot.assignment.actor.{VehicleActor, VehicleReaderShard, VehicleWriterShard}
import iot.assignment.publisher.AkkaPublisherService

class ActorModule(val akkaModule: AkkaModule) {

  import akkaModule._

  implicit val clusterSharding: ClusterSharding = ClusterSharding(system)

  val vehicleShardReaderProxy: ActorRef =
    clusterSharding.startProxy(
      typeName = VehicleReaderShard.shardName,
      role = Some("reader"),
      extractEntityId = VehicleReaderShard.idExtractor,
      extractShardId = VehicleReaderShard.shardResolver)

  private val snapshotInterval = config.configuration.getString("akka.persistence.snapshot-store.interval")

  val publisher = new AkkaPublisherService(vehicleShardReaderProxy)

  val vehicleShard: ActorRef = clusterSharding.start(
    typeName = VehicleWriterShard.shardName,
    entityProps = VehicleActor.props(publisher, snapshotInterval.toInt),
    settings = ClusterShardingSettings(system),
    extractEntityId = VehicleWriterShard.idExtractor,
    extractShardId = VehicleWriterShard.shardResolver
  )


}
