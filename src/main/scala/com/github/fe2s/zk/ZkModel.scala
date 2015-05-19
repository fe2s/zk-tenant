package com.github.fe2s.zk

/**
 * @author Oleksiy_Dyagilev
 */
object ZkModel {

  object Path {

    trait PathNode {
      def path(): String
    }

    object RootPath extends PathNode {
      def path() = "/app"
    }

    class ClientPath(id: String) extends PathNode {
      override def path() = RootPath.path() + s"/$id"
    }

    class AppServerSlotsPath(clientId: String) extends ClientPath(clientId) {
      override def path() = super.path() + "/app-server-slots"
    }

    class DbPath(clientId: String) extends ClientPath(clientId) {
      override def path() = super.path() + "/db"
    }

    class AppServerPrefixPath(clientId: String) extends ClientPath(clientId) {
      override def path(): String = super.path() + s"/${AppServerPrefixPath.nodePrefix}"
    }

    object AppServerPrefixPath {
      val nodePrefix = "app-server#"
    }

    class AppServerPath(clientId: String, appServerId: String) extends ClientPath(clientId) {
      override def path(): String = super.path() + s"/$appServerId"
    }


    implicit def nodeToPathString(node: PathNode): String = node.path()
  }

  object Domain {
    case class Application(clients:Seq[Client]){
      def prettyPrint() = {
        for (client <- clients) {
          println("/" + client.id)
          for (appServer <- client.appServers)
            println("\t" + appServer.hostPort)
        }
      }
    }
    case class Client(id:String, appServers:Seq[AppServer])
    case class AppServer(hostPort:String)
  }
}
