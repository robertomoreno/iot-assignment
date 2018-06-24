package iot.assignment.entrypoint

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.util.Timeout
import iot.assignment.repository.VehicleRepository

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.language.postfixOps

class VehicleReaderApi(vehicleRepository: VehicleRepository)(implicit system: ActorSystem) extends VehicleJsonProtocol{

  private implicit val materializer    : ActorMaterializer        = ActorMaterializer()
  private implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  private implicit val timeout         : Timeout                  = Timeout(10 seconds)

  val route: Route =
    get {
      path("vehicle" / "stats" / Segment) { id =>
        complete {
          vehicleRepository.getVehicleState(id)
        }
      }
    }

  def run(port: Int, host: String): Future[Http.ServerBinding] =
    Http().bindAndHandle(route, host, port)

}