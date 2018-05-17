package com.sfsurfer.blockchain

import com.sfsurfer.blockchain.entities._
import com.sfsurfer.blockchain.server.BlockchainServer

case class BlockchainNode(server: BlockchainServer) {
  def run() = {
    server.run()
    initializeBlockchain()
  }

  private[this] def getInitialBlock(): Block = Block(1, System.currentTimeMillis(), Vector.empty[Transaction], ProofValue(Some(1)), "")

  private[this] def initializeBlockchain(): Unit = Blockchain.blockchain.chain match {
    case c if c.isEmpty => println("Initializing Blockchain with first node"); Blockchain.addBlock(getInitialBlock()); ()
    case _ => () // do nothing
  }

  /**
    * TODO: How to discover neighbors, get chain
    *          Have an Primary Node as Authority for newly initialized nodes
    *          New nodes will pull initial chain and neighbor list from here
    *          Will have to be set in configuration (?)
    */

}
