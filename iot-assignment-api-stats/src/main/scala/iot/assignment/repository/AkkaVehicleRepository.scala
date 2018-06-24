package iot.assignment.repository

import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
import iot.assignment.model.Vehicle.{FetchVehicleState, VehicleStatus}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.language.postfixOps

class AkkaVehicleRepository(dataSource: ActorRef)(implicit system: ActorSystem) extends VehicleRepository {

  private implicit val ec     : ExecutionContextExecutor = system.dispatcher
  private implicit val timeout: Timeout                  = Timeout(10 seconds)

  override def getVehicleState(id: String): Future[Option[VehicleStatus]] =
    (dataSource ? FetchVehicleState(id)).mapTo[Option[VehicleStatus]]
}
