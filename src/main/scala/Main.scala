import org.apache.zookeeper.{WatchedEvent, Watcher, ZooKeeper}

/**
 * @author fe2s
 */
object Main extends App {

  lazy val zk = new ZooKeeper("localhost:2181", 60, null)

  run()

  def run() = {
    subscribe()
    Thread.sleep(6000000)
  }

  def subscribe() =  {
    zk.getChildren("/test", DataMonitor, null)
  }

  object DataMonitor extends Watcher {

    override def process(event: WatchedEvent): Unit = {
      println(event)
      subscribe()
    }
  }

}

