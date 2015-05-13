package com.github.fe2s.zk

/**
 * @author Oleksiy_Dyagilev
 */
object ServiceRegistryMessages {

  sealed trait Messages
  case class ServiceStarted(host: String, port: Int) extends Messages
  case class ServiceStopped(host: String, port: Int) extends Messages

}
