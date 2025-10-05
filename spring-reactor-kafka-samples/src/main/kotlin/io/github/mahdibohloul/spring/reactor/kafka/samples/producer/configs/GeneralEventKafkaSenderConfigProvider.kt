package io.github.mahdibohloul.spring.reactor.kafka.samples.producer.configs

import io.github.mahdibohloul.spring.reactor.kafka.producer.KafkaSenderConfigurationProvider
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.kafka.support.serializer.JsonSerializer
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kafka.sender.SenderOptions

@Component
class GeneralEventKafkaSenderConfigProvider<TValue : Any>(
  @Value("\${kafka.bootstrap-servers:localhost:9092}")
  private val bootstrapServers: String,
) : KafkaSenderConfigurationProvider<String, TValue> {

  override fun provide(): Mono<ReactiveKafkaProducerTemplate<String, TValue>> = Mono.fromCallable {
    val producerProps = mapOf(
      ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
      ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
      ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java,
      ProducerConfig.ACKS_CONFIG to "all",
      ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG to false,
    )

    val senderOptions = SenderOptions.create<String, TValue>(producerProps)
    ReactiveKafkaProducerTemplate(senderOptions)
  }
}
