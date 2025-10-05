package io.github.mahdibohloul.spring.reactor.kafka.producer

import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import reactor.core.publisher.Mono

/**
 * Provides a configuration mechanism for obtaining instances of `ReactiveKafkaProducerTemplate`
 * that can be used to send messages to Kafka topics.
 *
 * This interface defines a contract for building and providing Kafka producer templates
 * with a reactive programming model. It abstracts away the underlying configuration
 * details and ensures the producer templates are accessible in a reactive manner.
 *
 * @param TKey The type of the key for Kafka producer records.
 * @param TValue The type of the value for Kafka producer records.
 */
interface KafkaSenderConfigurationProvider<TKey : Any, TValue : Any> {
  fun provide(): Mono<ReactiveKafkaProducerTemplate<TKey, TValue>>
}
