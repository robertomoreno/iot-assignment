package iot.assignment

import iot.assignment.modules.{ActorModule, AkkaModule, ConfigModule}

object Boot extends App {

  val config     = new ConfigModule
  val akka       = new AkkaModule(config)
  val actors     = new ActorModule(akka)

}
