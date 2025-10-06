package io.github.mahdibohloul.spring.reactor.kafka.consumer

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Configuration properties for Reactor Kafka consumer settings.
 *
 * This data class is used for defining and accessing the properties
 * related to Reactor Kafka consumers within a Spring Boot application.
 * It allows enabling or disabling the consumer infrastructure
 * and listener discovery.
 *
 * This configuration can be adjusted via the `reactor.kafka.consumer`
 * property namespace in application properties or YAML files.
 *
 * Property reference:
 * - `reactor.kafka.consumer.enabled`: Specifies whether the consumer
 *   infrastructure and auto-discovery of `@KafkaController` listeners
 *   are enabled.
 *   Default is `false`.
 *
 * Used in conjunction with `ReactorKafkaConsumerAutoConfiguration`
 * to setup and manage Kafka consumers.
 *
 * @see io.github.mahdibohloul.spring.reactor.kafka.producer.autoconfigure.ReactorKafkaProducerAutoConfiguration
 * @see io.github.mahdibohloul.spring.reactor.kafka.consumer.discovery.KafkaConsumerDiscovery
 * @see io.github.mahdibohloul.spring.reactor.kafka.consumer.annotaitons.KafkaController
 */
@ConfigurationProperties("reactor.kafka.consumer")
data class ConsumerProperties(
  /** Whether to enable consumer auto-configuration and listener discovery. */
  val enabled: Boolean = false,
)
