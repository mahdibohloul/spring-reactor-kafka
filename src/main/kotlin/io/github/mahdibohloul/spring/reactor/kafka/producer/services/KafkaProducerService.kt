package io.github.mahdibohloul.spring.reactor.kafka.producer.services

import io.github.mahdibohloul.spring.reactor.kafka.producer.KafkaMessage
import reactor.core.publisher.Mono
import reactor.kafka.sender.SenderResult

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
   * Sends a Kafka message to the specified topic in an asynchronous and reactive manner.
   *
   * @param TMessage The type of the payload in the Kafka message.
   * @param kafkaMessage The Kafka message, encapsulating the topic, payload, headers, key generation logic,
   *                     and sender configuration provider.
   * @return A [Mono] emitting a [SenderResult] that provides details about the sending operation when completed.
   *         The operation result indicates success or failure in delivering the message.
   */
  fun <TMessage : Any> send(kafkaMessage: KafkaMessage<TMessage>): Mono<SenderResult<Void>>
}
