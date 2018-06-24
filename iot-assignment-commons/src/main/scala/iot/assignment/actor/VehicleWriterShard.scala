package iot.assignment.actor

import akka.cluster.sharding.ShardRegion
import iot.assignment.model.Vehicle.VehicleCommand

object VehicleWriterShard {

  val idExtractor: ShardRegion.ExtractEntityId = {
    case event: VehicleCommand => (event.id, event)
  }

  val numberOfShards = 1000

  val shardResolver: ShardRegion.ExtractShardId = {
    case event: VehicleCommand => getShardRegion(event.id)
  }

  val shardName: String = "vehicle-shard-writer"

  private def getShardRegion(id: String) = (math.abs(id.hashCode) % numberOfShards).toString
}
