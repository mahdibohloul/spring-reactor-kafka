package io.github.mahdibohloul.spring.reactor.kafka.consumer

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("reactor.kafka.consumer")
data class ConsumerProperties(
  val enabled: Boolean = false,
)
