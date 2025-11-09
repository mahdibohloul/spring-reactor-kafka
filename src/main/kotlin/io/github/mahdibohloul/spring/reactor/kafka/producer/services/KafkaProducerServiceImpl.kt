package io.github.mahdibohloul.spring.reactor.kafka.producer.services

import io.github.mahdibohloul.spring.reactor.kafka.producer.KafkaMessage
import io.github.mahdibohloul.spring.reactor.kafka.producer.KafkaSenderConfigurationProvider
import io.github.mahdibohloul.spring.reactor.kafka.producer.generators.KeyGenerator
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.RecordHeader
import org.springframework.context.ApplicationContext
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import reactor.core.publisher.Mono
import reactor.kafka.sender.SenderResult
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2
import kotlin.reflect.KClass

/**
 * Implementation of [KafkaProducerService] for sending messages to Kafka topics in a reactive manner.
 *
 * This class provides the functionality to dynamically generate message keys, resolve appropriate
 * Kafka sender templates, and send messages asynchronously to specified topics.
 *
 * The implementation uses the Spring ApplicationContext to dynamically retrieve instances of key generators
 * and sender configuration providers, ensuring flexibility and decoupling of dependencies.
 *
 * @constructor Creates an instance of [KafkaProducerServiceImpl].
 * @param applicationContext The Spring ApplicationContext used for retrieving key generator and
 *                            sender configuration provider beans.
 * @see KafkaProducerServiceMockImpl
 */
class KafkaProducerServiceImpl(
  private val applicationContext: ApplicationContext,
) : KafkaProducerService {
  override fun <TMessage : Any> send(
    kafkaMessage: KafkaMessage<TMessage>,
  ): Mono<SenderResult<Void>> = getSenderTemplate<TMessage>(kafkaMessage.senderConfigurationProviderClass)
    .zipWith(Mono.fromCallable { getKeyGeneratorInstance(kafkaMessage) })
    .flatMap { (template, keyGenerator) ->
      template.send(
        ProducerRecord(
          kafkaMessage.topic.topicName,
          null,
          null,
          keyGenerator.generateKey(kafkaMessage.topic, kafkaMessage.message),
          kafkaMessage.message.payload,
          kafkaMessage.message.headers.map { RecordHeader(it.key, it.value.toString().toByteArray()) },
        ),
      )
    }

  private fun <TMessage : Any> getSenderTemplate(
    senderConfigurationProvider: KClass<out KafkaSenderConfigurationProvider<*, *>>,
  ): Mono<out ReactiveKafkaProducerTemplate<String, TMessage>> = Mono.defer {
    @Suppress("UNCHECKED_CAST")
    val provider = applicationContext.getBean(
      senderConfigurationProvider.java,
    ) as KafkaSenderConfigurationProvider<String, TMessage>

    provider.provide()
  }

  @Suppress("UNCHECKED_CAST")
  private fun <TMessage : Any> getKeyGeneratorInstance(
    kafkaMessage: KafkaMessage<TMessage>,
  ): KeyGenerator<TMessage> = kafkaMessage.keyGenerator
    ?: applicationContext.getBean(kafkaMessage.keyGeneratorClass!!.java) as KeyGenerator<TMessage>
}
