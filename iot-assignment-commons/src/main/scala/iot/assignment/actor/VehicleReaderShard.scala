package iot.assignment.actor

import akka.cluster.sharding.ShardRegion
import iot.assignment.model.Vehicle.{FetchVehicleState, VehicleEvent}

object VehicleReaderShard {

  val idExtractor: ShardRegion.ExtractEntityId = {
    case event: VehicleEvent => (event.id, event)
    case query: FetchVehicleState => (query.id, query)
  }

  val numberOfShards = 1000

  val shardResolver: ShardRegion.ExtractShardId = {
    case event: VehicleEvent => getShardRegion(event.id)
    case query: FetchVehicleState => getShardRegion(query.id)
  }

  val shardName: String = "vehicle-shard-reader"

  private def getShardRegion(id: String) = (math.abs(id.hashCode) % numberOfShards).toString
}
