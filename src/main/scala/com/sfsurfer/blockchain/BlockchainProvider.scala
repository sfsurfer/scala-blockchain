package com.sfsurfer.blockchain

import com.sfsurfer.blockchain.entities._
import com.sfsurfer.blockchain.entities.rest._
import com.sfsurfer.blockchain.proof.Proof

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class BlockchainProvider(id: String, client: BlockchainClient) {
  import BlockchainProvider._
  import Blockchain._

  var neightbors: Set[Node] = Set.empty[Node]
  def addNeighbor(node: Node) { neightbors +: node.url }

  def mine(): NewBlockSuccess = {
    println("Mining new block...")
    val currentBlock: Block = blockchain.currentBlock
    val lastBlock = blockchain.lastBlock.getOrElse(handleNoBlocks())
    val lastProof = lastBlock.proof.proof.getOrElse(0)
    val proof = Proof.proofOfWork(lastProof)
    val transaction = Transaction("0", id, 1)
    val newBlock = Block(lastBlock.index + 1, System.currentTimeMillis(), Vector(transaction), proof, lastBlock.hash)
    Blockchain.addBlock(newBlock)
    NewBlockSuccess(lastBlock.hash, currentBlock.index, proof, lastBlock.transactions)
  }

  def addTransaction(t: Transaction): Future[AddTransactionResponse] = {
    blockchain.currentBlock.addTransaction(t) // TODO: Make more robust
    Future {
      AddTransactionResponse(AddTransactionSuccessStr(blockchain.currentBlock.index))
    }
  }

  def getChain: GetChainResponse =
    GetChainResponse(blockchain.chain, blockchain.chain.size)

  def valid_chain(chain: Vector[Block]): Boolean = {
    !chain.zipWithIndex.exists{
      case (b,i) if i < blockchain.chain.size - 1 => b.hash != blockchain.chain(i+1).previousHash
    }
  }

  def resolveConflicts(): ResolveConflictsResponse = {
    val replaced = neightbors.map { n =>
      for {
        neighborChain <- client.getNeighborChain(n.url)
        isLonger <- if (neighborChain.size > blockchain.size) Future(true) else Future(false)
        isValid <- if (isLonger) Future(valid_chain(blockchain.chain)) else Future(false)
        replaced <- if (isLonger && isValid) {
          println(s"Replacing chain with chain from ${n.url}")
          Blockchain(blockchain.chain)
          Future(true)
        } else { Future(false) }
      } yield replaced
    }.contains(Future(true))
    if (replaced) {
      ResolveConflictsResponse(ResolveConflictsResponse.ChainReplacedMsg, blockchain.chain)
    } else {
      ResolveConflictsResponse(ResolveConflictsResponse.ChainAuthoritativeMsg, blockchain.chain)
    }
  }

  def handleNoBlocks() = throw new Exception("No blocks!!!")
}

object BlockchainProvider {
  val AddTransactionSuccessStr: Long => String = { i => s"Successfully added transaction to block $i"}
}