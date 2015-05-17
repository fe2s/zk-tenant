package com.github.fe2s.zk

/**
 * @author Oleksiy_Dyagilev
 */
object ZkWatcher extends App {

  ZkServices.watch()

  Thread.sleep(500000)

}
