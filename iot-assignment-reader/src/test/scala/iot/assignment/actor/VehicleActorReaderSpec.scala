package iot.assignment.actor

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import iot.assignment.model.Vehicle._
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class VehicleActorReaderSpec
  extends TestKit(ActorSystem("VehicleActorReaderSpec"))
  with ImplicitSender
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll {


  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "A VehicleActorReader actor" must {

    "initialize status properly" in {
      val id = "0"
      val reader = system.actorOf(VehicleActorReader.props(), id)

      reader ! FetchVehicleState(id)
      expectMsg(Some(VehicleStatus(id, 0, Position(0, 0), 100)))
    }

    "process properly fuel events" in {
      val id = "1"
      val reader = system.actorOf(VehicleActorReader.props(), id)

      reader ! VehicleFuelStatusEvent(id, System.currentTimeMillis(), 50)
      reader ! FetchVehicleState(id)
      expectMsg(Some(VehicleStatus(id, 0, Position(0, 0), 50)))

      reader ! VehicleFuelStatusEvent(id, System.currentTimeMillis(), 20)
      reader ! FetchVehicleState(id)
      expectMsg(Some(VehicleStatus(id, 0, Position(0, 0), 20)))
    }

    "process properly position events" in {

      val id = "2"
      val reader = system.actorOf(VehicleActorReader.props(), id)

      val position = Position(4, 3)
      reader ! VehiclePositionEvent(id, System.currentTimeMillis(), position)
      reader ! FetchVehicleState(id)
      expectMsg(Some(VehicleStatus(id, 5, position, 100)))

      val newPosition = Position(0,0)
      reader ! VehiclePositionEvent(id, System.currentTimeMillis(), newPosition)
      reader ! FetchVehicleState(id)
      expectMsg(Some(VehicleStatus(id, 10, newPosition, 100)))
    }

    "process properly mixed events" in {
      val id = "3"
      val reader = system.actorOf(VehicleActorReader.props(), id)

      reader ! VehicleFuelStatusEvent(id, System.currentTimeMillis(), 50)
      reader ! FetchVehicleState(id)
      expectMsg(Some(VehicleStatus(id, 0, Position(0, 0), 50)))


      val position = Position(4, 3)
      reader ! VehiclePositionEvent(id, System.currentTimeMillis(), position)
      reader ! FetchVehicleState(id)
      expectMsg(Some(VehicleStatus(id, 5, position, 50)))
    }
  }
}
