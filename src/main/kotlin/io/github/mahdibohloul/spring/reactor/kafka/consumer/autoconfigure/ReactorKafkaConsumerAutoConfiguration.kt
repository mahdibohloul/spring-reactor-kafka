package io.github.mahdibohloul.spring.reactor.kafka.consumer.autoconfigure

import io.github.mahdibohloul.spring.reactor.kafka.autoconfigure.ReactiveKafkaAutoConfiguration
import io.github.mahdibohloul.spring.reactor.kafka.consumer.ConsumerProperties
import io.github.mahdibohloul.spring.reactor.kafka.consumer.discovery.KafkaConsumerDiscovery
import io.github.mahdibohloul.spring.reactor.kafka.consumer.services.KafkaConsumerService
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan

/**
 * Auto-configuration class for integrating Reactor Kafka consumers into a Spring Boot application.
 *
 * This class enables the discovery and registration of Kafka listener methods annotated with `@ReactiveKafkaListener`
 * within beans annotated as Kafka controllers (`@KafkaController`).
 * It relies on `ConsumerProperties` for configuration,
 * and performs its operations in conjunction with `KafkaConsumerService` and `KafkaConsumerDiscovery`.
 *
 * The primary responsibilities of this configuration are:
 * - Scanning for and setting up Kafka consumer infrastructure
 * post the initialization of `ReactiveKafkaAutoConfiguration`.
 * - Facilitating the automatic discovery and registration of Kafka listeners.
 * - Ensuring the required dependencies for Kafka consumers are properly configured in the application context.
 *
 * This class is activated with the `@AutoConfiguration` annotation,
 * indicating it is part of Spring's auto-configuration mechanism.
 * The `@EnableConfigurationProperties` annotation binds application
 * configuration properties to the `ConsumerProperties` data class, ensuring the consumer-specific settings
 *  can be customized via application properties.
 *
 * Associated with:
 * - `ConsumerProperties` for configuring consumer infrastructure enablement and discovery.
 * - `KafkaConsumerDiscovery` for managing listener registration logic.
 * - `ReactiveKafkaAutoConfiguration` to act as a dependency for ensuring Kafka basics are
 * initialized before this configuration.
 */
@EnableConfigurationProperties(ConsumerProperties::class)
@AutoConfiguration(after = [ReactiveKafkaAutoConfiguration::class])
@ComponentScan(basePackages = ["io.github.mahdibohloul.spring.reactor.kafka.consumer"])
class ReactorKafkaConsumerAutoConfiguration {
  /**
   * Configures and provides an instance of `KafkaConsumerDiscovery` for managing the discovery and registration
   * of Kafka listener methods.
   */
  @Bean
  fun autoConsumerDiscovery(
    applicationContext: ApplicationContext,
    kafkaConsumerService: KafkaConsumerService,
  ): KafkaConsumerDiscovery = KafkaConsumerDiscovery(
    applicationContext = applicationContext,
    kafkaConsumerService = kafkaConsumerService,
  )
}
