package com.github.fe2s.zk

/**
 * @author Oleksiy_Dyagilev
 */
object ZkSchemaBuilder extends App {

  implicit val zk = ZkServices.startZkClient()
  ZkServices.buildSchema()

}
