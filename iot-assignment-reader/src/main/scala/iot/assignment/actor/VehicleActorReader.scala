package iot.assignment.actor

import akka.actor.{Actor, ActorLogging, Props}
import iot.assignment.actor.VehicleActorReader.StatusHandler
import iot.assignment.model.Vehicle._

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._
import scala.language.postfixOps

// TODO Implement a proper Database persistence
class VehicleActorReader extends Actor with ActorLogging {

  implicit val ec: ExecutionContextExecutor = context.system.dispatcher

  //This actor stops if does not receive a message in 30 minutes.
  context.setReceiveTimeout(30 minutes)

  private val vehicleId: String        = context.self.path.name
  private val status   : StatusHandler = new StatusHandler(vehicleId)

  override def receive: Receive = {
    case fuel: VehicleFuelStatusEvent ⇒
      log.info("Received event: " + fuel)
      status updatedFuelStatus fuel

    case position: VehiclePositionEvent ⇒
      log.info("Received event: " + position)
      status updatedPositionStatus position

    case FetchVehicleState(_) =>
      sender ! Option(status.get)
  }
}

object VehicleActorReader {

  val name: String = "vehicleReader"

  def props(): Props = Props(new VehicleActorReader)

  class StatusHandler(id: String) {

    var status = VehicleStatus(id, 0, Position(0, 0), 100)

    def get = status

    def updatedFuelStatus(fuel: VehicleFuelStatusEvent) = {
      status = status.copy(fuelTankCurrentStatus = fuel.fuelStatusPercent)
    }

    def updatedPositionStatus(position: VehiclePositionEvent) = {
      import scala.math._
      val newDistance = sqrt(
        pow(position.position.x - status.lastLocation.x, 2) + pow(position.position.y - status.lastLocation.y, 2)
      )
      status = status.copy(
        totalDistance = (newDistance + status.totalDistance).toFloat,
        lastLocation = position.position
      )
    }
  }
}
