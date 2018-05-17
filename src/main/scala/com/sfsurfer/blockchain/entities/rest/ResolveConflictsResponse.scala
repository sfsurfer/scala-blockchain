package com.sfsurfer.blockchain.entities.rest

import com.sfsurfer.blockchain.entities.Block

case class ResolveConflictsResponse(message: String, chain: Vector[Block])

object ResolveConflictsResponse extends ((String, Vector[Block]) => ResolveConflictsResponse) {
  val ChainReplacedMsg = "Our chain was replaced"
  val ChainAuthoritativeMsg = "Our chain was authoritative"
}
