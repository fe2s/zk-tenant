package com.github.fe2s.zk

import com.github.fe2s.Config
import com.github.fe2s.zk.ZkModel.Domain.{AppServer, Application, Client}
import com.github.fe2s.zk.ZkModel.Path._
import com.github.fe2s.zk.ZkUtils.ShortCuts._
import com.github.fe2s.zk.ZkUtils._
import org.apache.curator.framework.api.CuratorEvent
import org.apache.curator.framework.{CuratorFramework, CuratorFrameworkFactory}
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.zookeeper.CreateMode

import scala.collection.JavaConversions._
import scala.util.Random

/**
 * @author Oleksiy_Dyagilev
 */
object ZkServices {

  def buildSchema() = {
    println("(re)creating ZK schema")
    implicit val zk = startZkClient()

    zkDeleteIfExists(RootPath)

    (1 to Config.clientsNumber) map { id =>
      val clientId = s"client-$id"
      val appServerSlots = Random.nextInt(Config.maxAppServerSlots)
      zkCreate.forPath(new AppServerSlotsPath(clientId), appServerSlots.toString.getBytes)

      val dbUrl = s"jdbc://db-client-$id:5555"
      zkCreate.forPath(new DbPath(clientId), dbUrl.getBytes)
    }
  }


  /**
   * register this app server and return db url
   */
  def registerAppServer(host: String, port: Int): Option[String] = {
    implicit val zk = startZkClient()

    println("looking for a client with free slots")
    val clients = zk.getChildren.forPath(RootPath)
    val foundClient = clients.find { clientId =>
      val slots = new String(zk.getData.forPath(new AppServerSlotsPath(clientId))).toInt
      val appServersNumber = findClientAppServersIds(clientId).size
      println(s"client $clientId slots $slots appServersNumber $appServersNumber")
      slots > appServersNumber
    }

    // TODO: lock
    // register app server
    foundClient.map { clientId =>
      println(s"Found client $clientId with available slot(s) ... registering")
      val serviceHostPort = s"$host:$port"
      zkCreate.withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(new AppServerPrefixPath(clientId), serviceHostPort.getBytes)

      val dbUrl = new String(zk.getData.forPath(new DbPath(clientId)))
      dbUrl
    }
  }

  def watch(callback: Application => Any): Unit = {
    implicit val zk = startZkClient()

    val listener = (client: CuratorFramework, event: CuratorEvent) => {
      println(Thread.currentThread() +  " event " + event)
      if (event.getPath != null) {
        zk.getChildren.watched().forPath(event.getPath)
        val currentModel = readModel()
        callback(currentModel)
      }
      ()
    }

    zk.getCuratorListenable.addListener(listener)

    def watchClient(clientId: String) = zk.getChildren.watched().forPath(new ClientPath(clientId))
    def watchApp() = zk.getChildren.watched().forPath(RootPath)

    val clients = watchApp()
    clients.foreach { clientId =>
      watchClient(clientId)
    }

    // block
    this.synchronized(wait())
  }

  private def readModel()(implicit zk:CuratorFramework): Application = {
    val clients =
      for (clientId <- zk.getChildren.forPath(RootPath)) yield {

      val appServers =
        for (appServerId <- findClientAppServersIds(clientId)) yield {
          val hostPort = zk.getData.forPath(new AppServerPath(clientId, appServerId))
          AppServer(new String(hostPort))
        }

      Client(clientId, appServers)
    }

    Application(clients)
  }

  private def findClientAppServersIds(clientId:String)(implicit zk:CuratorFramework) = {
    zk.getChildren.forPath(new ClientPath(clientId)).filter(_.startsWith(AppServerPrefixPath.nodePrefix))
  }


  private def startZkClient(): CuratorFramework = {
    val retryPolicy = new ExponentialBackoffRetry(1000, 3)
    val zk = CuratorFrameworkFactory.newClient(Config.zkConnectString, retryPolicy)
    zk.start()
    zk.blockUntilConnected()
    zk
  }


}
