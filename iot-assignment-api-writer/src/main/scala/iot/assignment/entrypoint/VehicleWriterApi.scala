package iot.assignment.entrypoint

import akka.actor.{ActorRef, ActorSystem}
import akka.cluster.Cluster
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.util.Timeout
import iot.assignment.model.Vehicle.{VehicleCommand, VehicleFuelStatusComnd, VehiclePositionCmnd}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.language.postfixOps

class VehicleWriterApi(vehicleShard: ActorRef)(implicit system: ActorSystem) extends VehicleJsonProtocol{

  private implicit val materializer    : ActorMaterializer        = ActorMaterializer()
  private implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  private implicit val timeout         : Timeout                  = Timeout(10 seconds)

  val route: Route =
    get {
      path("status") {
        complete(Cluster(system).state.toString)
      }
    } ~
    post {
      pathPrefix("vehicle" / "status") {
        path("fuel") {
          decodeRequest {
            entity(as[VehicleFuelStatusComnd])(handleEvent)
          }
        } ~
        path("position") {
          decodeRequest {
            entity(as[VehiclePositionCmnd])(handleEvent)
          }
        }
      }
    }

  def run(port: Int, host: String): Future[Http.ServerBinding] =
    Http().bindAndHandle(route, host, port)

  // TODO ensure that the command is handle by the Actor and it is not lost
  private def handleEvent(action: VehicleCommand) = {
    vehicleShard ! action
    complete("{}")
  }
}