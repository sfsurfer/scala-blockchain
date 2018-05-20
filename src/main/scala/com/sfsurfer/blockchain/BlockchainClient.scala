package com.sfsurfer.blockchain

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.sfsurfer.blockchain.entities.rest._

import scala.concurrent.Future

class BlockchainClient extends JsonSupport {
  import BlockchainClient._
  implicit val system = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext = system.dispatcher

  def registerNode(uri: String, request: RegisterNodeRequest): Future[RegisterNodeResponse] = {
    val url: Uri = Uri.from(scheme = "http", host = "localhost", path = RegisterUrl, port = 8080)
    Marshal(request).to[RequestEntity] flatMap { entity =>
      val req = HttpRequest(method = HttpMethods.POST, uri = url, entity = entity.withContentType(ContentTypes.`application/json`))
      val response = Http().singleRequest(req).map(_.entity.withContentType(ContentTypes.`application/json`))
      response.flatMap(Unmarshal(_).to[RegisterNodeResponse])
    }
  }

  def getNeighborChain(uri: String): Future[GetChainResponse] = {
    val response: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = uri))
    response.flatMap(Unmarshal(_).to[GetChainResponse])
  }
}

object BlockchainClient {
  val RegisterUrl = "/nodes/register"
  val ChainUrl = "/chain"
}