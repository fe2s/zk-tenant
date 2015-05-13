package com.github.fe2s.zk

/**
 * @author Oleksiy_Dyagilev
 */
object ServiceRegistryMessages {

  sealed trait Messages
  case class ServiceStarted(host: String, port: Int)
  case class ServiceStopped(host: String, port: Int)

}
