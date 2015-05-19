package com.github.fe2s.zk

import akka.actor.Actor
import com.github.fe2s.zk.ServiceRegistryMessages._

/**
 * @author Oleksiy_Dyagilev
 */
class ServiceRegistryActor extends Actor {

  implicit val zk = ZkServices.startZkClient()

  override def receive = {
    case ServiceStarted(host, port) =>
      println(s"registering service available at $host:$port")
      val dbUrl = ZkServices.registerAppServer(host, port)
      sender ! dbUrl

    case ServiceStopped => ???
  }

}
