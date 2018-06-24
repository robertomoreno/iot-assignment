package iot.assignment.actor

import akka.routing.ConsistentHashingRouter.ConsistentHashMapping
import iot.assignment.model.Vehicle

object VehicleEventHash {

  def hashMapping: ConsistentHashMapping = {
    case Vehicle.VehicleFuelStatusEvent(id, _, _) ⇒ id
    case Vehicle.VehicleFuelStatusEvent(id, _, _) ⇒ id
  }

}
