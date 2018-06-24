package iot.assignment

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import iot.assignment.model.Vehicle.{Position, VehicleStatus}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

package object entrypoint {

  trait VehicleJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {

    implicit val positionFormat: RootJsonFormat[Position] = jsonFormat2(Position)

    implicit val vehicleStatus: RootJsonFormat[VehicleStatus] = jsonFormat4(VehicleStatus)
  }


}
