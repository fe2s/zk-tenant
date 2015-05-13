package com.github.fe2s.service

/**
 * @author Oleksiy_Dyagilev
 */
object HttpServiceMessages {

  sealed trait Messages
  case class HttpServiceConfig(dbUrl:String) extends Messages

}
