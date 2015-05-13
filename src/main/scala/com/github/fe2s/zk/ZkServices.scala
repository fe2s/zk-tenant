package com.github.fe2s.zk

import com.github.fe2s.Config
import org.apache.curator.framework.{CuratorFramework, CuratorFrameworkFactory}
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.zookeeper.CreateMode

import scala.Predef
import scala.collection.JavaConversions._
import scala.util.Random

/**
 * @author Oleksiy_Dyagilev
 */
object ZkServices {

  object ZkModel {

    trait Node {
      def path(): String
    }

    object Root extends Node {
      def path() = "/app"
    }

    class Client(id: String) extends Node {
      override def path() = Root.path() + s"/$id"
    }

    class AppServerSlots(clientId: String) extends Client(clientId) {
      override def path() = super.path() + "/app-server-slots"
    }

    class Db(clientId: String) extends Client(clientId) {
      override def path() = super.path() + "/db"
    }

    class AppServer(clientId: String) extends Client(clientId) {
      override def path(): String = super.path() + s"/${AppServer.nodePrefix}"
    }

    object AppServer {
      val nodePrefix = "app-server#"
    }


    implicit def nodeToPathString(node: Node): String = node.path()
  }


  import ZkModel._

  def buildSchema() = {
    println("(re)creating ZK schema")
    val zk = startZkClient()

    if (zk.checkExists().forPath(Root) != null) {
      zk.delete().deletingChildrenIfNeeded().forPath(Root)
    }

    (1 to Config.clientsNumber) map { id =>
      val clientId = s"client-$id"
      val appServerSlots = Random.nextInt(Config.maxAppServerSlots)
      zk.create().creatingParentsIfNeeded().forPath(new AppServerSlots(clientId), appServerSlots.toString.getBytes)

      val dbUrl = "host:port"
      zk.create().creatingParentsIfNeeded().forPath(new Db(clientId), dbUrl.getBytes)
    }
  }


  /**
   * register this app server and return db url
   */
  def registerAppServer(host: String, port: Int): Option[String] = {
    val zk = startZkClient()

    println("looking for a client with free slots")
    val clients = zk.getChildren.forPath(Root)
    val foundClient = clients.find { clientId =>
      val slots = new String(zk.getData.forPath(new AppServerSlots(clientId))).toInt
      val appServersNumber = zk.getChildren.forPath(new Client(clientId)).count(_.startsWith(AppServer.nodePrefix))
      println(s"client $clientId slots $slots appServersNumber $appServersNumber")
      slots > appServersNumber
    }

    // TODO: lock
    // register app server
    foundClient.map { clientId =>
      println(s"Found client $clientId with available slot(s) ... registering")
      val serviceUrlData = s"http://$host:$port"
      zk.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(new AppServer(clientId), serviceUrlData.getBytes)

      val dbUrl = new String(zk.getData.forPath(new Db(clientId)))
      dbUrl
    }
  }

  private def startZkClient(): CuratorFramework = {
    val retryPolicy = new ExponentialBackoffRetry(1000, 3)
    val zk = CuratorFrameworkFactory.newClient(Config.zkConnectString, retryPolicy)
    zk.start()
    zk
  }

}
