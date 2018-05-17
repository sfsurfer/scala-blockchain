package com.sfsurfer.blockchain.entities.rest

import com.sfsurfer.blockchain.entities.{ProofValue, Transaction}

case class NewBlockSuccess(hash: String,
                           index: Long,
                           proof: ProofValue,
                           transactions: Vector[Transaction],
                           message: String = "New Block Forged")
