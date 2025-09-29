package io.github.mahdibohloul.spring.reactor.kafka.consumer

import reactor.core.publisher.Mono

/**
 * Interface for providing Kafka receiver configurations in a Spring Boot application.
 *
 * This interface defines a contract for classes that supply [KafkaReceiverConfiguration] instances,
 * which contain all the necessary configuration parameters for setting up a Kafka receiver.
 * Implementations of this interface are typically used in conjunction with
 * [io.github.mahdibohloul.spring.reactor.kafka.consumer.annotaitons.KafkaController]
 * and [io.github.mahdibohloul.spring.reactor.kafka.consumer.annotaitons.ReactiveKafkaListener] annotations
 * to configure Kafka message consumption.
 *
 * @param TKey The type parameter for the message key
 * @param TValue The type parameter for the message value
 *
 * Usage example:
 * ```
 * class MyConfigProvider : KafkaReceiverConfigurationProvider<String, MyMessage> {
 *     override fun provide(): Mono<KafkaReceiverConfiguration<String, MyMessage>> {
 *         return Mono.just(KafkaReceiverConfiguration(
 *             topic = myTopic,
 *             keyConfiguration = keyConfig,
 *             valueConfiguration = valueConfig,
 *             consumerConfig = consumerSettings
 *         ))
 *     }
 * }
 * ```
 *
 * @see KafkaReceiverConfiguration For the configuration structure
 * @see io.github.mahdibohloul.spring.reactor.kafka.consumer.annotaitons.KafkaController
 * For the controller annotation that uses this provider
 * @see io.github.mahdibohloul.spring.reactor.kafka.consumer.annotaitons.ReactiveKafkaListener
 * For the method-level annotation that uses this provider
 */
interface KafkaReceiverConfigurationProvider<TKey : Any, TValue : Any> {
  fun provide(): Mono<KafkaReceiverConfiguration<TKey, TValue>>
}
