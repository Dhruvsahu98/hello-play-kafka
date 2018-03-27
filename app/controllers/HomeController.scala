package controllers

import akka.stream.scaladsl.{Flow, Sink, Source}
import javax.inject._
import play.api.mvc.{InjectedController, WebSocket}
import services.{Cache, Kafka}

import scala.concurrent.Future


@Singleton
class HomeController @Inject() (kafka: Kafka, cache: Cache) extends InjectedController {

  def cacheSize = Action {
    Ok(cache.buffer.size.toString)
  }

  def index(group: String) = Action { implicit request =>
    Ok(views.html.index(routes.HomeController.ws(group).webSocketURL()))
  }

  def ws(group: String) = WebSocket.acceptOrResult[Any, String] { _ =>
    val source = group match {
      case "cache" => Source(cache.buffer.toList)
      case _ => kafka.source("RandomNumbers", group).map(_.value)
    }

    val flow = Flow.fromSinkAndSource(Sink.ignore, source)
    Future.successful(Right(flow))
  }

}
