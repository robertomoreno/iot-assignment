package iot.assignment.repository

import iot.assignment.model.Vehicle.VehicleStatus

import scala.concurrent.Future

trait VehicleRepository {

  def getVehicleState(id: String): Future[Option[VehicleStatus]]
}

