package io.github.mahdibohloul.spring.reactor.kafka.producer.autoconfigure

import io.github.mahdibohloul.spring.reactor.kafka.autoconfigure.ReactiveKafkaAutoConfiguration
import io.github.mahdibohloul.spring.reactor.kafka.producer.ProducerProperties
import io.github.mahdibohloul.spring.reactor.kafka.producer.annotations.OnKafkaProducerEnabled
import io.github.mahdibohloul.spring.reactor.kafka.producer.commands.dispatchers.KafkaDispatcher
import io.github.mahdibohloul.spring.reactor.kafka.producer.pubsub.publishers.KafkaPublisher
import io.github.mahdibohloul.spring.reactor.kafka.producer.services.KafkaProducerService
import io.github.mahdibohloul.spring.reactor.kafka.producer.services.KafkaProducerServiceImpl
import io.github.mahdibohloul.spring.reactor.kafka.producer.services.KafkaProducerServiceMockImpl
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import

/**
 * Auto-configuration class for setting up Kafka producer components in a Reactor-based Spring environment.
 *
 * This class enables the registration of Kafka producer-related beans and configurations. It depends on
 * the presence of `ReactiveKafkaAutoConfiguration` and the configuration properties specified in
 * `ProducerProperties`. This auto-configuration will initialize appropriate Kafka producer service
 * implementation beans based on the application configuration.
 *
 * The `KafkaPublisher` and `KafkaDispatcher` components are imported as part of this configuration to assist
 * with publishing and command dispatching for Kafka-based notifications and commands.
 */
@EnableConfigurationProperties(ProducerProperties::class)
@AutoConfiguration(after = [ReactiveKafkaAutoConfiguration::class])
@Import(KafkaPublisher::class, KafkaDispatcher::class)
class ReactorKafkaProducerAutoConfiguration {
  /**
   * Creates and registers a bean of type [KafkaProducerService] when Kafka producer functionality is enabled.
   *
   * The resulting bean utilizes the provided [ApplicationContext] to access necessary dependencies
   * during runtime and provides capabilities for producing Kafka messages in a reactive manner.
   *
   * @param applicationContext The Spring [ApplicationContext], used for resolving dependencies
   *                            such as key generators and sender configuration providers required by
   *                            the Kafka producer service.
   * @return An instance of [KafkaProducerService], backed by the implementation [KafkaProducerServiceImpl],
   *         which provides reactive Kafka message production functionality.
   */
  @Bean
  @OnKafkaProducerEnabled
  fun kafkaProducerService(
    applicationContext: ApplicationContext,
  ): KafkaProducerService = KafkaProducerServiceImpl(applicationContext)

  /**
   * Provides a mocked implementation of [KafkaProducerService] for testing or non-production environments.
   *
   * This method creates and registers a bean of type [KafkaProducerServiceMockImpl] when there is no existing
   * bean of type [KafkaProducerServiceImpl] in the application context. The mock implementation simulates
   * message sending behavior without actually interacting with a Kafka broker.
   *
   * @return An instance of [KafkaProducerServiceMockImpl].
   */
  @Bean
  @ConditionalOnMissingBean(KafkaProducerServiceImpl::class)
  fun mockKafkaProducerService(): KafkaProducerService = KafkaProducerServiceMockImpl()
}
