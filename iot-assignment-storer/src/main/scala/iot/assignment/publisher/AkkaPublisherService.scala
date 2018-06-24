package iot.assignment.publisher
import akka.actor.ActorRef

import scala.concurrent.Future

class AkkaPublisherService(actors: ActorRef*) extends PublisherService {

  override def publishMessage[T](message: T): Boolean = {
    actors.foreach(_ ! message)
    true
  }

  override def publishMessageAsync[T](message: T): Future[Boolean] =
    Future.successful(publishMessage(message))
}
