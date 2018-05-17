import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}

import scala.concurrent.Future
import scala.util.{Failure, Success}
import com.typesafe.config.ConfigFactory

val conf = ConfigFactory.load()
implicit val system = ActorSystem()
implicit val materializer = ActorMaterializer()
// needed for the future flatMap/onComplete in the end
implicit val executionContext = system.dispatcher


val response: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = "localhost:8080/chain"))
response.onComplete{
  case Success(res) if res.status.intValue() == 200 => {
    println("Success")
  }
  case Success(res) => {
    sys.error(s"Got unexpected status ${res.status}")
  }
  case Failure(_) => sys.error("Failed to get response")
}
