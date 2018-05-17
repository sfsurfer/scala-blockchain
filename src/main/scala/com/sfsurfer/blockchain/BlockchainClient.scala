package com.sfsurfer.blockchain

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.sfsurfer.blockchain.entities.rest.{GetChainResponse, JsonSupport}

import scala.concurrent.Future

class BlockchainClient extends JsonSupport {
  implicit val system = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext = system.dispatcher

  def getNeighborChain(uri: String): Future[GetChainResponse] = {
    val response: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = uri))
    response.flatMap(Unmarshal(_).to[GetChainResponse])
//    response.onComplete{
//      case Success(HttpResponse(status, headers, entity, _)) if status.intValue() == 200 => {
//        Unmarshal(response).to[GetChainResponse]
//      }
//      case Success(res) => {
//        throw new Exception(s"Got unexpected status ${res.status}")
//      }
//      case Failure(_) => throw new Exception("Failed to get response")
//    }
  }
}

object BlockchainClient {
  val ChainUrl = "/chain"
}