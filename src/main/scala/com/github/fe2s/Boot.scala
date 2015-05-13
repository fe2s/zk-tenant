package com.github.fe2s

/**
 * @author Oleksiy_Dyagilev
 */

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.github.fe2s.service.MyServiceActor
import com.github.fe2s.zk.ServiceRegistryActor
import com.github.fe2s.zk.ServiceRegistryMessages.ServiceStarted

import spray.can.Http

import scala.concurrent.duration._

object Boot extends App {

  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("on-spray-can")

  // create and start our service actor
  val service = system.actorOf(Props[MyServiceActor], "demo-service")

  val serviceRegistry = system.actorOf(Props[ServiceRegistryActor], "service-registry")

  // TODO: get interface, generate port
  val host = "localhost"
  val port = 8080

  implicit val timeout = Timeout(5.seconds)

  IO(Http) ? Http.Bind(service, host, port)

  // TODO: move to service
  serviceRegistry ! ServiceStarted(host, port)
}
