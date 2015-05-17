package com.github.fe2s.zk

import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.api.{CuratorEvent, CuratorListener}

/**
 * @author Oleksiy_Dyagilev
 */
object ZkUtils {

  object ShortCuts {
    def zkCreate(implicit zk:CuratorFramework) = zk.create().creatingParentsIfNeeded()
    def zkDelete(implicit zk:CuratorFramework) = zk.delete().deletingChildrenIfNeeded()
    def zkDeleteIfExists(path:String)(implicit zk:CuratorFramework) = if (zk.checkExists().forPath(path) != null) zkDelete.forPath(path)
  }



  implicit def functionToListener(f: (CuratorFramework, CuratorEvent) => Unit): CuratorListener = {
    new CuratorListener {
      override def eventReceived(client: CuratorFramework, event: CuratorEvent): Unit = f(client, event)
    }
  }

}
