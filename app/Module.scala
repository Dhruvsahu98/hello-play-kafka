import com.google.inject.AbstractModule
import services.{Cache, Kafka, KafkaImpl}

class Module extends AbstractModule {

  override def configure() = {
    bind(classOf[Kafka]).to(classOf[KafkaImpl]).asEagerSingleton()
    bind(classOf[Cache]).asEagerSingleton()
  }

}
