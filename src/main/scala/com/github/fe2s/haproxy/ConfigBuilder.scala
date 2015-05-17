package com.github.fe2s.haproxy

import com.github.fe2s.zk.ZkModel.Domain._

import scala.util.Properties.lineSeparator


/**
 * TODO: use template engine
 *
 * @author Oleksiy_Dyagilev
 */
class ConfigBuilder {

  val defaults =
    """
      |defaults
      |  mode http
      |  timeout connect 5000ms
      |  timeout client 50000ms
      |  timeout server 50000ms
    """.stripMargin

  def build(app: Application): String = {
    frontend(app) + lineSeparator*2 + backends(app)
  }

  private def frontend(app: Application): String = {
    val header = """frontend http-in
                   |  bind *:8080""".stripMargin

    def acl(clientId: String) =
      s"|  acl $clientId-path path_beg /$clientId".stripMargin

    def useBacked(clientId: String) =
      s"|  use_backend $clientId-backend if $clientId-path".stripMargin

    val acls = app.clients.map(client => acl(client.id))
    val useBackends = app.clients.map(client => useBacked(client.id))

    Seq(Seq(header), acls, useBackends).flatten.mkString(lineSeparator)
  }

  private def backend(client: Client): String = {
    val header =
      s"""|backend ${client.id}-backend
          |  balance roundrobin""".stripMargin

    def server(hostPort: String) = s"|  server app $hostPort".stripMargin

    val servers = client.appServers.map(appServer => server(appServer.hostPort))

    Seq(Seq(header), servers).flatten.mkString(lineSeparator)
  }

  private def backends(app: Application):String = {
    app.clients.map(backend).mkString(lineSeparator * 2)
  }


}

