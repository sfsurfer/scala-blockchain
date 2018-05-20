package com.sfsurfer.blockchain.entities.rest

import com.sfsurfer.blockchain.entities.Node

case class RegisterNodeResponse(message: String, nodes: Set[Node])
