package iot.assignment

import iot.assignment.modules._

object Boot extends App {

  val config     = new ConfigModule
  val akka       = new AkkaModule(config)
  val actor      = new ActorModule(akka)
  val repository = new RepositoryModule(actor)
  val entryPoint = new EntryPointModule(repository)

}
