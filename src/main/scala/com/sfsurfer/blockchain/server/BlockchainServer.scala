package com.sfsurfer.blockchain.server

import akka.http.scaladsl.Http
import com.sfsurfer.blockchain.BlockchainProvider

import scala.io.StdIn

case class BlockchainServer(provider: BlockchainProvider) extends Routes {
  def run() = {
    val port: Int = 8081 // TODO: Move to config
    val bindingFuture = Http().bindAndHandle(route, "localhost")
    StdIn.readLine()
    bindingFuture.flatMap(_.unbind()).onComplete(_ => system.terminate)
  }
}
