package io.github.mahdibohloul.spring.reactor.kafka.consumer.annotaitons

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty

/**
 * This annotation is used to mark a bean to be initialized when the Kafka consumer is enabled.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
@ConditionalOnProperty(
  value = ["reactor.kafka.consumer.enabled"],
  havingValue = "true",
  matchIfMissing = false,
)
annotation class OnKafkaConsumerEnabled
