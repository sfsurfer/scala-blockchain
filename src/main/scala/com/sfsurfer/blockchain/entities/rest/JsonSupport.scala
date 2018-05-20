package com.sfsurfer.blockchain.entities.rest

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.sfsurfer.blockchain.Blockchain
import com.sfsurfer.blockchain.entities._
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val proofValueFormat = jsonFormat1(ProofValue)
  implicit val transactionFormat = jsonFormat3(Transaction)
  implicit val newBlockForgedFormat = jsonFormat5(NewBlockSuccess)
  implicit val blockFormat = jsonFormat5(Block)
  implicit val blockchainFormat = jsonFormat1(Blockchain)
  implicit val getChainResponseFormat = jsonFormat2(GetChainResponse)
  implicit val addTransactionResponseFormat = jsonFormat1(AddTransactionResponse)
  implicit val addTransactionFailureFormat = jsonFormat1(AddTransactionFailure)
  implicit val nodeFormat = jsonFormat2(Node)
  implicit val registerNodeRequestFormat = jsonFormat1(RegisterNodeRequest)
  implicit val registerNodeResponseFormat = jsonFormat2(RegisterNodeResponse)
  implicit val resolveConflictsResponseFormat = jsonFormat2(ResolveConflictsResponse)
}
