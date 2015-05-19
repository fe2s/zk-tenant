package com.github.fe2s.haproxy

import java.nio.file.{Files, Paths}

import com.github.fe2s.Config
import com.github.fe2s.zk.ZkModel.Domain.Application
import com.github.fe2s.zk.ZkServices

/**
 * @author Oleksiy_Dyagilev
 */
object HAProxyAgent extends App {

  println("Starting HAProxy agent " + Thread.currentThread())

  implicit val zk = ZkServices.startZkClient()

  val initial = ZkServices.readModel()
  reactOnChange(initial)
  
  ZkServices watchChanges reactOnChange
  
  def reactOnChange(app:Application) = {
    app.prettyPrint()
    rewriteConfig(app)
    reloadConfig()
  }
  
  def rewriteConfig(app:Application) = {
    println("Rewriting HAProxy config " + Config.haproxyConfFile)
    val configStr = new ConfigBuilder().build(app)
    Files.write(Paths.get(Config.haproxyConfFile), configStr.getBytes)
  }

  def reloadConfig() = {
    import scala.sys.process._
    val output = Config.haproxyReloadScript.!!
    println(output)
  }

}
