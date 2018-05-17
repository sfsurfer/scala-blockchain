package com.sfsurfer.blockchain.entities.rest

import com.sfsurfer.blockchain.entities.Block

case class GetChainResponse(chain: Vector[Block], size: Long)