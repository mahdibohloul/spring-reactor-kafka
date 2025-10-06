package io.github.mahdibohloul.spring.reactor.kafka.producer

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Configuration properties for Reactor Kafka producer settings.
 * This class encapsulates properties that control the behavior of the Reactor Kafka producer infrastructure.
 *
 * The properties are prefixed with `reactor.kafka.producer` in the Spring configuration.
 *
 * @property enabled Flag indicating whether to enable producer autoconfiguration and producer service beans.
 * The default value is `false`.
 * When the value is false, all functionality will be worked except the producer which uses the mock implementation.
 */
@ConfigurationProperties("reactor.kafka.producer")
data class ProducerProperties(
  /** Whether to enable producer autoconfiguration and producer service beans. */
  val enabled: Boolean = false,
)
