package com.github.fe2s

/**
 * @author Oleksiy_Dyagilev
 */

import java.net.ServerSocket

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

  val host = "localhost"
  val port = findAvailablePort()
//  val port = 8082

  implicit val timeout = Timeout(15.seconds)

  IO(Http) ? Http.Bind(service, host, port)

  Thread.sleep(1000)

  val dbUrlFuture = serviceRegistry ? ServiceStarted(host, port)

  dbUrlFuture onFailure {
    case t => println("Failed to get reply from service registry actor " + t)
  }

  for (dbUrlOpt <- dbUrlFuture.mapTo[Option[String]]) yield {
    for (dbUrl <- dbUrlOpt) yield {
      println("sending dbUrl " + dbUrl)
      service ! HttpServiceConfig(dbUrl)
    }
  }

  // subject for race conditions
  def findAvailablePort(): Int = new ServerSocket(0).getLocalPort


}
