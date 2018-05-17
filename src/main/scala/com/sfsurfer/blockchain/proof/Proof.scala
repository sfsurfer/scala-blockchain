package com.sfsurfer.blockchain.proof

import java.security.MessageDigest

import com.sfsurfer.blockchain.entities.ProofValue

object Proof {
  def proofOfWork(lastProof: Int): ProofValue = {
    ProofValue(Range(0, Int.MaxValue).find{ i =>
      validateProof(lastProof, i)
    })
  }

  private[this] def validateProof(lastProof: Int, proof: Int): Boolean = {
    val str = (lastProof * proof).toString.getBytes("UTF-8")
    val digest: Array[String] = MessageDigest.getInstance("SHA-256").digest(str).map("%02x".format(_))
    digest.takeWhile(_ == "00").length >= 2
  }
}
