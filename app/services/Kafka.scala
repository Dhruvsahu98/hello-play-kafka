package services

import akka.kafka.scaladsl.{Consumer, Producer}
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.{Sink, Source}
import javax.inject.{Inject, Singleton}
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import play.api.Configuration

trait Kafka {
  def sink: Sink[ProducerRecord[String, String], _]
  def source(topic: String, group: String): Source[ConsumerRecord[String, String], _]
}

@Singleton
class KafkaImpl @Inject() (configuration: Configuration) extends Kafka {

  import akka.kafka.ProducerSettings
  import org.apache.kafka.common.serialization.StringSerializer

  val kafkaUrl = "localhost:9092"

  def producerSettings: ProducerSettings[String, String] = {
    val serializer = new StringSerializer()
    val config = configuration.getOptional[Configuration]("akka.kafka.producer").getOrElse(Configuration.empty)

    ProducerSettings[String, String](config.underlying, serializer, serializer)
      .withBootstrapServers(kafkaUrl)
  }

  def consumerSettings(group: String): ConsumerSettings[String, String] = {
    val deserializer = new StringDeserializer()
    val config = configuration.getOptional[Configuration]("akka.kafka.consumer").getOrElse(Configuration.empty)

    ConsumerSettings(config.underlying, deserializer, deserializer)
      .withBootstrapServers(kafkaUrl)
      .withGroupId(group)
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
  }

  def sink: Sink[ProducerRecord[String, String], _] =
    Producer.plainSink(producerSettings)

  def source(topic: String, group: String): Source[ConsumerRecord[String, String], _] =
    Consumer.plainSource(consumerSettings(group), Subscriptions.topics(topic))

}
