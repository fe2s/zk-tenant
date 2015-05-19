package com.github.fe2s.haproxy

import com.github.fe2s.zk.ZkServices

/**
 * @author Oleksiy_Dyagilev
 */
object HAProxyAgent extends App {

  println("Starting HAProxy agent " + Thread.currentThread())

  ZkServices.watch { app =>
    app.prettyPrint()
  }

}
