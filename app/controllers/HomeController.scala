package controllers

import akka.stream.scaladsl.{Flow, Sink}
import javax.inject._
import play.api.mvc.{InjectedController, WebSocket}
import services.Kafka

import scala.concurrent.Future


@Singleton
class HomeController @Inject() (kafka: Kafka) extends InjectedController {

  def index(group: String) = Action { implicit request =>
    Ok(views.html.index(routes.HomeController.ws(group).webSocketURL()))
  }

  def ws(group: String) = WebSocket.acceptOrResult[Any, String] { _ =>
    val kafkaSource = kafka.source("RandomNumbers", group)
    val flow = Flow.fromSinkAndSource(Sink.ignore, kafkaSource.map(_.value))
    Future.successful(Right(flow))
  }

}
