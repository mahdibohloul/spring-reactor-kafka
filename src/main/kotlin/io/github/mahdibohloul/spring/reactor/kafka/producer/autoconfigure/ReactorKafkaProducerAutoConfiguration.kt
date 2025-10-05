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

@EnableConfigurationProperties(ProducerProperties::class)
@AutoConfiguration(after = [ReactiveKafkaAutoConfiguration::class])
@Import(KafkaPublisher::class, KafkaDispatcher::class)
class ReactorKafkaProducerAutoConfiguration {
  @Bean
  @OnKafkaProducerEnabled
  fun kafkaProducerService(
    applicationContext: ApplicationContext,
  ): KafkaProducerService = KafkaProducerServiceImpl(applicationContext)

  @Bean
  @ConditionalOnMissingBean(KafkaProducerServiceImpl::class)
  fun mockKafkaProducerService(): KafkaProducerService = KafkaProducerServiceMockImpl()
}
