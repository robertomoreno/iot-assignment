package iot.assignment

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import iot.assignment.model.Vehicle.{Position, VehicleFuelStatusComnd, VehiclePositionCmnd}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

package object entrypoint {

  trait VehicleJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {

    implicit val positionFormat: RootJsonFormat[Position] = jsonFormat2(Position)

    implicit val fuelFormat: RootJsonFormat[VehicleFuelStatusComnd] = jsonFormat3(VehicleFuelStatusComnd)

    implicit val gpsFormat: RootJsonFormat[VehiclePositionCmnd] = jsonFormat3(VehiclePositionCmnd)
  }


}
