package io.github.mahdibohloul.spring.reactor.kafka.producer.services

import io.github.mahdibohloul.spring.reactor.kafka.producer.KafkaSenderConfigurationProvider
import io.github.mahdibohloul.spring.reactor.kafka.producer.KafkaTopic
import io.github.mahdibohloul.spring.reactor.kafka.producer.generators.KeyGenerator
import org.apache.kafka.clients.producer.RecordMetadata
import org.slf4j.LoggerFactory
import org.springframework.messaging.Message
import reactor.core.publisher.Mono
import reactor.kafka.sender.SenderResult
import reactor.kotlin.core.publisher.toMono
import java.lang.Exception
import kotlin.reflect.KClass

class KafkaProducerServiceMockImpl : KafkaProducerService {
  private val logger = LoggerFactory.getLogger(this::class.java)

  override fun <TMessage : Any> send(
    topic: KafkaTopic,
    keyGeneratorBeanClass: KClass<out KeyGenerator<*>>,
    message: Message<TMessage>,
    senderConfigurationProvider: KClass<out KafkaSenderConfigurationProvider<*, *>>,
  ): Mono<SenderResult<Void>> = message.toMono()
    .doOnNext { logger.info(generateMockLogMessage(topic, message.payload)) }
    .then(mockSenderResult.toMono())

  private fun generateMockLogMessage(
    topic: KafkaTopic,
    it: Any?,
  ): String = "Sending a message using mock producer on topic $topic: $it"

  companion object {
    private val mockSenderResult: SenderResult<Void> = object : SenderResult<Void> {
      override fun correlationMetadata(): Void? = null
      override fun exception(): Exception? = null
      override fun recordMetadata(): RecordMetadata? = null
    }
  }
}
