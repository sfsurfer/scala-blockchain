package com.sfsurfer.blockchain.entities

import java.security.MessageDigest

case class Block(index: Long, timestamp: Long, transactions: Vector[Transaction], proof: ProofValue, previousHash: String) {
  def hash: String =
    MessageDigest.getInstance("SHA-256")
      .digest(this.toString.getBytes("UTF-8"))
      .map("%02x".format(_)).mkString

  def addTransaction(transaction: Transaction): Block =
    this.copy(transactions = transactions :+ transaction)
}

