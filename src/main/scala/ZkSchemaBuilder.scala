import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.ExponentialBackoffRetry

import scala.util.Random

/**
 * @author Oleksiy_Dyagilev
 */
object ZkSchemaBuilder extends App {

  ZkServices.buildSchema()

}
