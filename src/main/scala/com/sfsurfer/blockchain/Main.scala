package com.sfsurfer.blockchain

import java.util.UUID

import com.sfsurfer.blockchain.server.BlockchainServer

object Main {
  def main(args: Array[String]): Unit = {
    // TODO: Move id to configuration
    val id: String = UUID.randomUUID().toString.replace("-","")
    val client: BlockchainClient = new BlockchainClient
    val provider: BlockchainProvider = BlockchainProvider(id, client)
    val server = BlockchainServer(provider)
    val node = BlockchainNode(server)
    node.run()
  }
}
