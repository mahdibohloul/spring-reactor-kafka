package io.github.mahdibohloul.spring.reactor.kafka.consumer.factories

import io.github.mahdibohloul.spring.reactor.kafka.consumer.KafkaReceiverConfiguration
import io.github.mahdibohloul.spring.reactor.kafka.consumer.keyType
import io.github.mahdibohloul.spring.reactor.kafka.consumer.topics
import io.github.mahdibohloul.spring.reactor.kafka.consumer.valueType
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.kafka.receiver.KafkaReceiver

@Component
class KafkaReceiverFactory {
  private val logger = LoggerFactory.getLogger(this::class.java)
  private val kafkaReceiverMap: ConcurrentMap<String, KafkaReceiver<*, *>> = ConcurrentHashMap()

  @Suppress("UNCHECKED_CAST")
  fun <TKey : Any, TValue : Any> getKafkaReceiver(
    config: KafkaReceiverConfiguration<TKey, TValue>,
  ): KafkaReceiver<TKey, TValue> {
    val kafkaReceiver = kafkaReceiverMap.computeIfAbsent(computeKafkaReceiverName(config)) {
      logger.info("Initializing Kafka receiver for topic ${config.topics}")
      return@computeIfAbsent KafkaReceiver.create(config.receiverOption)
        .also { logger.info("Kafka receiver for topic ${config.topics} initialized") }
    }

    if (kafkaReceiver is KafkaReceiver<*, *>) {
      return kafkaReceiver as KafkaReceiver<TKey, TValue>
    }

    error(
      "Kafka receiver for topic ${config.topics} is not of the expected type ${config.keyType}, ${config.valueType}",
    )
  }

  private fun computeKafkaReceiverName(configuration: KafkaReceiverConfiguration<*, *>): String =
    KAFKA_RECEIVER_NAME_FORMAT.format(configuration.name)

  companion object {
    private const val KAFKA_RECEIVER_NAME_FORMAT = "KafkaReceiver-%s"
  }
}
