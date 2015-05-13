package com.github.fe2s.service

import akka.actor.Actor
import com.github.fe2s.service.HttpServiceMessages._
import spray.http.MediaTypes._
import spray.routing._

class HttpServiceActor extends Actor with HttpService {

  def actorRefFactory = context

  def httpRoute(dbUrl: Option[String]) =
    path("") {
      get {
        respondWithMediaType(`text/html`) { // XML is marshalled to `text/xml` by default, so we simply override here
          complete {
            <html>
              <body>
                <h1>OK. Http service with db url: {dbUrl}</h1>
              </body>
            </html>
          }
        }
      }
    }

  val handleMessage: Receive = {
    case HttpServiceConfig(dbUrl) =>
      println(s"Configuring HttpService with dbUrl $dbUrl")
      val withConfigBehaviour = runRoute(httpRoute(Some(dbUrl))) orElse handleMessage
      context.become(withConfigBehaviour)
  }

  def receive = runRoute(httpRoute(dbUrl =  None)) orElse handleMessage
}

