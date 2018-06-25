package iot.assignment

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, RequestEntity}
import akka.stream.ActorMaterializer
import iot.assignment.model.Vehicle.{Position, VehicleCommand, VehicleFuelStatusComnd, VehiclePositionCmnd}

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Random, Try}

object Boot extends App with VehicleJsonProtocol{

  implicit val system          : ActorSystem              = ActorSystem()
  implicit val materializer    : ActorMaterializer        = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  val http = Http()

  val clientNumber = args.toList.headOption
    .flatMap(head => Try(head.toInt).toOption)
    .getOrElse(3)

  def sendUpdateCommand(path: String, entity: RequestEntity) ={
    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = path,
      entity = entity)
    system.log.info("making request: " + request)
    http.singleRequest(request)
  }

  private def scheduleNext(e: VehicleFuelStatusComnd) = {
    system.scheduler.scheduleOnce(1 second) {
      val newEvent = e.copy(
        timestamp = System.currentTimeMillis,
        fuelStatusPercent = e.fuelStatusPercent - new Random().nextFloat()
      )
      generateCall(newEvent)
    }
  }

  private def scheduleNext(e: VehiclePositionCmnd) = {
    system.scheduler.scheduleOnce(1 second) {
      val newEvent = e.copy(
        timestamp = System.currentTimeMillis,
        position = Position(
          e.position.x - 10 + new Random().nextFloat()*20,
          e.position.y - 10 + new Random().nextFloat()*20
        )
      )
      generateCall(newEvent)
    }
  }

  def generateCall(cmand: VehicleCommand): Unit = {
    cmand match {
      case e: VehicleFuelStatusComnd =>
        Marshal(e).to[RequestEntity].flatMap(entity =>
          sendUpdateCommand("http://localhost:8080/vehicle/status/fuel", entity)
        )
        scheduleNext(e)

      case e: VehiclePositionCmnd =>
        Marshal(e).to[RequestEntity].flatMap(entity =>
          sendUpdateCommand("http://localhost:8080/vehicle/status/position", entity)
        )
        scheduleNext(e)
    }
  }

// ---- RUN ----

  (1 to clientNumber).foreach { id =>
    val timestamp = System.currentTimeMillis
    val fuel = VehicleFuelStatusComnd(id.toString, timestamp, 100)
    generateCall(fuel)
    val position = VehiclePositionCmnd(id.toString, timestamp, Position(0, 0))
    generateCall(position)
  }

}
