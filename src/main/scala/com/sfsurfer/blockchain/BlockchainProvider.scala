package com.sfsurfer.blockchain

import com.sfsurfer.blockchain.entities._
import com.sfsurfer.blockchain.entities.rest._
import com.sfsurfer.blockchain.proof.Proof

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

case class BlockchainProvider(id: String, client: BlockchainClient) {
  import BlockchainProvider._
  import Blockchain._

  val authority = "127.0.0.1:8080"
  val url = "127.0.0.1:8080"
  val ourNode = Node(id,url)
  var allNodes: Vector[Node] = Vector(ourNode)
  def neighbors: Vector[Node] = allNodes.filter(_.id != ourNode.id)
  def addNeighbor(node: Node) { allNodes = allNodes :+ node }


  def registerNode(): Unit = {
    val registerUrl = authority + BlockchainClient.RegisterUrl
    val response = client.registerNode(registerUrl, RegisterNodeRequest(Vector(ourNode)))
    response.onComplete {
      case Success(r) => println("Registered Node"); allNodes = r.nodes.toVector // TODO: Add more intelligence here
      case Failure(f) => println(s"Failed to register node: $f")
    }
  }

  def mine(): NewBlockSuccess = {
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
    println("Resolving conflicts")
    val replaced = neighbors.map { n =>
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