package iot.assignment.model

object Vehicle {

  case class Position(x: Float, y: Float)

  // Command and Events

  sealed trait VehicleCommand {
    val id       : String
    val timestamp: Long
  }

  final case class VehicleFuelStatusComnd(id: String, timestamp: Long, fuelStatusPercent: Float) extends VehicleCommand

  final case class VehiclePositionCmnd(id: String, timestamp: Long, position: Position) extends VehicleCommand

  sealed trait VehicleEvent {
    val id: String
    val timestamp: Long
  }

  final case class VehicleFuelStatusEvent(id: String, timestamp: Long, fuelStatusPercent: Float) extends VehicleEvent

  final case class VehiclePositionEvent(id: String, timestamp: Long, position: Position) extends VehicleEvent


  final case class VehicleStatus(id: String,
                                 totalDistance: Float,
                                 lastLocation: Position,
                                 fuelTankCurrentStatus: Float)

  final case class FetchVehicleState(id: String)

}
