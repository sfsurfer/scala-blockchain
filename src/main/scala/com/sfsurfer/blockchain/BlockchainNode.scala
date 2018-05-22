package com.sfsurfer.blockchain

import com.sfsurfer.blockchain.entities._
import com.sfsurfer.blockchain.server.BlockchainServer

case class BlockchainNode(server: BlockchainServer) {
  def run() = {
    initializeBlockchain()
    server.run()
  }

  private[this] def getInitialBlock(): Block = Block(1, System.currentTimeMillis(), Vector.empty[Transaction], ProofValue(Some(1)), "")

  private[this] def initializeBlockchain(): Unit = {
    server.provider.registerNode()
    server.provider.resolveConflicts()
    Blockchain.getChain().chain match {
      case c if c.isEmpty => println("Initializing Blockchain with first node"); Blockchain.addBlock(getInitialBlock()); ()
      case _ => () // do nothing
    }
  }
}
