package com.github.fe2s.haproxy

import com.github.fe2s.zk.ZkModel.Domain._
import org.specs2.mutable.Specification

/**
 * @author Oleksiy_Dyagilev
 */
class ConfigBuilderTest extends Specification {

  "builder" should {

    "build config" in {
      val client1 = new Client("client-1", Seq(AppServer("localhost:8081"),AppServer("localhost:8082")))
      val client2 = new Client("client-2", Seq(AppServer("localhost:8083"),AppServer("localhost:8084")))

      val app = new Application(Seq(client1, client2))

      val conf = new ConfigBuilder().build(app)

      conf mustEqual
        """
          |frontend http-in
          |  bind *:8080
          |  acl client-1-path path_beg /client-1
          |  acl client-2-path path_beg /client-2
          |  use_backend client-1-backend if client-1-path
          |  use_backend client-2-backend if client-2-path
          |
          |backend client-1-backend
          |  balance roundrobin
          |  server app localhost:8081
          |  server app localhost:8082
          |
          |backend client-2-backend
          |  balance roundrobin
          |  server app localhost:8083
          |  server app localhost:8084
        """.stripMargin.trim
    }

  }

}
