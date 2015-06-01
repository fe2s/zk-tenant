package com.github.fe2s

/**
 * @author Oleksiy_Dyagilev
 */
object Config {

  val clientsNumber = 3
  val maxAppServerSlots = 3
  val zkConnectString = "localhost:2181"

  val haproxyConfFile = "/Users/fe2s/Projects/zk-tenant/haproxy/haproxy.conf"
  val haproxyReloadScript = "/Users/fe2s/Projects/zk-tenant/haproxy/reload-conf.sh"

}
