package com.github.fe2s

/**
 * @author Oleksiy_Dyagilev
 */

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.github.fe2s.service.HttpServiceActor
import com.github.fe2s.service.HttpServiceMessages.HttpServiceConfig
import com.github.fe2s.zk.ServiceRegistryActor
import com.github.fe2s.zk.ServiceRegistryMessages.ServiceStarted

import spray.can.Http

import scala.concurrent.duration._

object Boot extends App {

  implicit val system = ActorSystem("on-spray-can")
  implicit val context = system.dispatcher

  val service = system.actorOf(Props[HttpServiceActor], "demo-service")
  val serviceRegistry = system.actorOf(Props[ServiceRegistryActor], "service-registry")

  // TODO: get interface, generate port
  val host = "localhost"
  val port = 8080

  implicit val timeout = Timeout(5.seconds)

  IO(Http) ? Http.Bind(service, host, port)

  val dbUrlFuture = serviceRegistry ? ServiceStarted(host, port)

  for (dbUrlOpt <- dbUrlFuture.mapTo[Option[String]]) yield {
    for (dbUrl <- dbUrlOpt) yield {
      service ! HttpServiceConfig(dbUrl)
    }
  }

}
