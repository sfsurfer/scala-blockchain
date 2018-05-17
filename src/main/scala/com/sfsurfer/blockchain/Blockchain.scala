package com.sfsurfer.blockchain

import com.sfsurfer.blockchain.entities.Block

case class Blockchain(chain: Vector[Block]) {
  def size: Long = chain.length.toLong
  def currentBlock: Block = lastBlock.getOrElse(throw new Exception("No last block!!!"))
  def lastBlock: Option[Block] = chain match {
    case c if c.isEmpty => None
    case _ => Some(chain.last)
  }
}

object Blockchain extends (Vector[Block] => Blockchain) {
  var blockchain: Blockchain = this.apply(Vector.empty[Block])
  def apply(chain: Vector[Block]) = new Blockchain(chain)
  def addBlock(block: Block): Unit = {
    blockchain = blockchain.copy(chain = blockchain.chain :+ block)
  }
}
