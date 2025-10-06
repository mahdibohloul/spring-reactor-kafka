package io.github.mahdibohloul.spring.reactor.kafka.producer.services

import io.github.mahdibohloul.spring.reactor.kafka.producer.KafkaSenderConfigurationProvider
import io.github.mahdibohloul.spring.reactor.kafka.producer.KafkaTopic
import io.github.mahdibohloul.spring.reactor.kafka.producer.generators.KeyGenerator
import org.springframework.messaging.Message
import reactor.core.publisher.Mono
import reactor.kafka.sender.SenderResult
import kotlin.reflect.KClass

/**
 * Provides functionalities for sending messages to Kafka topics in a reactive manner.
 *
 * This interface defines the contract for producing Kafka messages with the key and sender
 * configuration being dynamically resolved or provided at runtime.
 * Implementing classes
 * handle the logic for constructing Kafka producer records and interacting with the
 * underlying Kafka producer templates.
 */
interface KafkaProducerService {
  /**
   * Sends a message to the specified Kafka topic using the provided configuration.
   *
   * This method utilizes a reactive programming model to asynchronously send a message
   * to a Kafka topic. It requires details about the topic, a key generator, the message payload,
   * and a configuration provider for the Kafka sender.
   *
   * @param TMessage The type of the message being sent.
   * @param topic The Kafka topic to which the message will be sent.
   * @param keyGeneratorBeanClass The class of the key generator used to generate the key for the message.
   * @param message The message to be sent, including headers and payload.
   * @param senderConfigurationProvider The class of the Kafka sender configuration provider
   *                                     used to obtain the reactive producer template.
   * @return A [Mono] representing the result of the message sending operation.
   *         It emits a [SenderResult] upon completion.
   */
  fun <TMessage : Any> send(
    topic: KafkaTopic,
    keyGeneratorBeanClass: KClass<out KeyGenerator<*>>,
    message: Message<TMessage>,
    senderConfigurationProvider: KClass<out KafkaSenderConfigurationProvider<*, *>>,
  ): Mono<SenderResult<Void>>
}
