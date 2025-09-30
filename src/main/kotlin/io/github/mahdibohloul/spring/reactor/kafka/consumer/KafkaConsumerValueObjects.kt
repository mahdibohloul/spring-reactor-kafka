package io.github.mahdibohloul.spring.reactor.kafka.consumer

import reactor.kafka.receiver.ReceiverOptions

/**
 * Represents the configuration required to initialize a Kafka receiver.
 *
 * This data class encapsulates all necessary parameters to set up a Kafka receiver,
 * including settings for managing subscription topics, scheduling, and instance identification.
 * It is used to define the behavior and runtime properties of a Kafka consumer.
 *
 * @param TKey The type parameter for the Kafka message key
 * @param TValue The type parameter for the Kafka message value
 * @property receiverOption The receiver options that define subscription topics,
 *                          deserializers, consumer properties, and other configurations
 * @property name The name of the Kafka receiver instance, allowing for identification in multi-receiver setups
 */
data class KafkaReceiverConfiguration<TKey : Any, TValue : Any>(
  val receiverOption: ReceiverOptions<TKey, TValue>,
  val name: String,
)
