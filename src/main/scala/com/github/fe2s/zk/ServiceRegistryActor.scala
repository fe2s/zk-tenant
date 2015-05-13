package com.github.fe2s.zk

import akka.actor.Actor
import akka.actor.Actor.Receive
import com.github.fe2s.zk.ServiceRegistryMessages._

/**
 * @author Oleksiy_Dyagilev
 */
class ServiceRegistryActor extends Actor {

  override def receive = {
    case ServiceStarted(host, port) =>
      println(s"registering $host:$port")
      val dbUrl = ZkServices.registerAppServer(host, port)
      sender ! dbUrl

    case ServiceStopped => ???
  }

}
