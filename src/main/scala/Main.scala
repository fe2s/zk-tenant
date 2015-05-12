import org.apache.curator.RetryPolicy
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.zookeeper.{CreateMode, WatchedEvent, Watcher, ZooKeeper}

/**
 * @author fe2s
 */
object Main extends App {

  val zkConnectString = "localhost:2181"
  
//  lazy val zk = new ZooKeeper("localhost:2181", 60, null)

  run()

  def run() = {
    ZkServices.registerAppServer("localhost", 8080)

//    client.create().withMode(CreateMode.EPHEMERAL)


//    subscribe()
//    Thread.sleep(6000000)
  }

//  def subscribe() =  {
//    zk.getChildren("/test", DataMonitor, null)
//  }
//
//  object DataMonitor extends Watcher {
//
//    override def process(event: WatchedEvent): Unit = {
//      println(event)
//      subscribe()
//    }
//  }

}

