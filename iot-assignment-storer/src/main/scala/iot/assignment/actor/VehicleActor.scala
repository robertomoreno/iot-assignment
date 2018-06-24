package iot.assignment.actor

import akka.actor.{ActorLogging, Props}
import akka.persistence.{PersistentActor, RecoveryCompleted, SnapshotOffer}
import iot.assignment.actor.VehicleActor.VehicleStatus
import iot.assignment.model.Vehicle._
import iot.assignment.publisher.PublisherService

import scala.concurrent.duration._
import scala.language.postfixOps

class VehicleActor(publisher: PublisherService,
                   snapshotInterval: Int) extends PersistentActor with ActorLogging {

  //This actor stops if does not receive a message in 30 minutes.
  context.setReceiveTimeout(30 minutes)

  private val vehicleId: String = context.self.path.name
  override def persistenceId: String = vehicleId
  log.info("Started actor: " + vehicleId)

  var status: VehicleStatus = VehicleStatus.empty

  val receiveRecover: Receive = {
    case event: VehicleFuelStatusEvent             ⇒ updateFuelState(event)
    case event: VehiclePositionEvent               ⇒ updatePositionState(event)
    case SnapshotOffer(_, snapshot: VehicleStatus) ⇒ status = snapshot
    case RecoveryCompleted                         ⇒ log.info("finish recovery with status: " + status)
  }

  val receiveCommand: Receive = {
    case e: VehicleFuelStatusComnd => handleFuelStatus(e)
    case e: VehiclePositionCmnd    => handleVehiclePosition(e)
  }

  private def updateFuelState(event: VehicleFuelStatusEvent) =
    status = status.copy(
      currentTimestamp = event.timestamp,
      currentFuelPercent = event.fuelStatusPercent
    )

  private def updatePositionState(event: VehiclePositionEvent) = {

    def calculateNewInactivePeriod =
      if (event.position == status.position) // Probably want to introduce an error range
        status.inactiveFrom + event.timestamp - status.currentTimestamp
      else
        0L

    // Just ignore disordered messages
    if (event.timestamp > status.currentTimestamp)
      status = status.copy(
        currentTimestamp = event.timestamp,
        inactiveFrom = calculateNewInactivePeriod,
        position = event.position
      )
  }

  /**
    * Want to avoid wrong events due to vehicle bounds on the road so we only
    * accept messages which its
    */
  def validateFuelEvent(fuelCmd: VehicleFuelStatusComnd): Boolean =
    status.currentFuelPercent == -1 ||
      fuelCmd.fuelStatusPercent >= 0 ||
      fuelCmd.fuelStatusPercent <= status.currentFuelPercent ||
      status.inactiveFrom > 20000 // 20 secs in millis


  /**
    * Save current state every X events where X is the number specified in `snapShotInterval` param
    */
  private def saveSnapshotIfRequired(): Unit =
    if (lastSequenceNr % snapshotInterval == 0 && lastSequenceNr != 0) {
      log.info("Saving snapshot: " + status)
      saveSnapshot(status)
    }

  private def handleVehiclePosition(vehicleCmnd: VehiclePositionCmnd): Unit = {
    log.info("Message received: " + vehicleCmnd.toString)
    val positionEvent = VehiclePositionEvent(vehicleCmnd.id, vehicleCmnd.timestamp, vehicleCmnd.position)
    persist(positionEvent) { event ⇒
      log.info("New status: " + status)
      updatePositionState(event)
      publisher.publishMessage(event)
      saveSnapshotIfRequired()
    }
  }

  private def handleFuelStatus(fuelComnd: VehicleFuelStatusComnd): Unit = {
    log.info("Message received: " + fuelComnd.toString)
    if (validateFuelEvent(fuelComnd)) {
      val fuelStatusEvent = VehicleFuelStatusEvent(fuelComnd.id, fuelComnd.timestamp, fuelComnd.fuelStatusPercent)
      persist(fuelStatusEvent) { event ⇒
        log.info("New status: " + status)
        updateFuelState(event)
        publisher.publishMessage(event)
        saveSnapshotIfRequired()
      }
    }
  }
}

object VehicleActor {

  def props(publisher: PublisherService, snapshotInterval: Int) =
    Props(new VehicleActor(publisher, snapshotInterval))

  // Status handler classes

  case class VehicleStatus(inactiveFrom: Long, currentFuelPercent: Float, currentTimestamp: Long, position: Position) {
    def isEmpty: Boolean =
      inactiveFrom == -1 && currentFuelPercent == -1 && currentTimestamp == -1 && position.x == -1 && position.y == -1
  }

  object VehicleStatus {
    def empty = new VehicleStatus(-1, -1, -1, Position(-1, -1))
  }

}

