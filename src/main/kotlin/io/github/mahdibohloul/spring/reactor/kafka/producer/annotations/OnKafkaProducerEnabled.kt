package io.github.mahdibohloul.spring.reactor.kafka.producer.annotations

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty

/**
 * This annotation is used to mark a bean to be initialized when the Kafka producer is enabled.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@MustBeDocumented
@ConditionalOnProperty(
  value = ["reactor.kafka.producer.enabled"],
  havingValue = "true",
  matchIfMissing = false,
)
annotation class OnKafkaProducerEnabled
