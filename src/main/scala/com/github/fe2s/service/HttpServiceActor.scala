package com.github.fe2s.service

import akka.actor.Actor
import com.github.fe2s.service.HttpServiceMessages._
import spray.http.MediaTypes._
import spray.routing._

class HttpServiceActor extends Actor with HttpService {

  def actorRefFactory = context

  def httpRoute(dbUrl: Option[String]) =
    pathSuffix("") {
      get {
        val message =  dbUrl match {
          case Some(url) => s"OK. Http service configured with db url: $url"
          case _ => "Http service is not configured with db url"
        }

        respondWithMediaType(`text/html`) {
          complete {
            <html>
              <body>
                <h1>{message}</h1>
              </body>
            </html>
          }
        }
      }
    }

  val handleConfigMessage: Receive = {
    case HttpServiceConfig(dbUrl) =>
      println(s"Configuring HttpService with dbUrl $dbUrl")
      val withConfigBehaviour = runRoute(httpRoute(Some(dbUrl))) orElse handleConfigMessage
      context.become(withConfigBehaviour)
  }

  def receive = runRoute(httpRoute(dbUrl =  None)) orElse handleConfigMessage
}

