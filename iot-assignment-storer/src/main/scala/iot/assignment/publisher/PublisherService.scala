package iot.assignment.publisher

import scala.concurrent.Future

trait PublisherService {

  def publishMessage [T] (message: T): Boolean

  def publishMessageAsync [T] (nessage: T): Future[Boolean]

}
