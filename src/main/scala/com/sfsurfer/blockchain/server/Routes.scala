package com.sfsurfer.blockchain.server

import akka.actor.ActorSystem
import akka.http.scaladsl.model.ContentTypes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.sfsurfer.blockchain.BlockchainProvider
import com.sfsurfer.blockchain.entities.Transaction
import com.sfsurfer.blockchain.entities.rest._
import spray.json._

import scala.concurrent.Future

trait Routes extends JsonSupport {

  val provider: BlockchainProvider
  implicit val system = ActorSystem("blockchain")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val mineRoute: Route = {
    get {
      path("mine") {
        complete(provider.mine().toJson)
      }
    }
  }

  val chainRoute: Route = {
    get {
      path("chain") {
        complete(provider.getChain.toJson)
      }
    }
  }

  val transactionRoute: Route = {
    post {
      path("transaction" / "create") {
        decodeRequest {
          entity(as[Transaction]) { transaction =>
            val saved: Future[AddTransactionResponse] = provider.addTransaction(transaction)
            onComplete(saved) { t =>
              complete(t.get.toJson)
            }
          }
        }
      }
    }
  }

  val nodeRoutes: Route = {
    post {
      path("nodes" / "register") {
        decodeRequest {
          entity(as[RegisterNodeRequest]) { nodes =>
            nodes.nodes.foreach {
              provider.addNeighbor
            }
            val response = RegisterNodeResponse(s"Registered ${nodes.nodes}", provider.neighbors.toSet).toJson
            println(response)
            complete(response)
          }
        }
      }
    } ~
    get {
      path("nodes" / "resolve") {
        complete(provider.resolveConflicts().toJson)
      }
    }
  }

  val route: Route = transactionRoute ~ mineRoute ~ chainRoute ~ nodeRoutes
}
