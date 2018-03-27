package services

import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import javax.inject.{Inject, Singleton}

import scala.collection.mutable.ListBuffer

@Singleton
class Cache @Inject()(kafka: Kafka,
                      implicit val materializer: Materializer) {

  val buffer = ListBuffer.empty[String]

  val sink = Sink.foreach[String](s => {
    buffer += s
    println(s"Buffer size: ${buffer.size}")
  })

  kafka.source("RandomNumbers", "cache")
    .map(_.value)
    .to(sink)
    .run()
}
