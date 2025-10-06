package io.github.mahdibohloul.spring.reactor.kafka.autoconfigure

import org.springframework.boot.autoconfigure.AutoConfiguration

/**
 * Base autoconfiguration marker for the Spring Reactor Kafka library.
 * Actual consumer and producer configurations depend on this configuration.
 */
@AutoConfiguration
class ReactiveKafkaAutoConfiguration
