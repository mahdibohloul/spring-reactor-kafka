package io.github.mahdibohloul.spring.reactor.kafka.producer

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("reactor.kafka.producer")
data class ProducerProperties(
  val enabled: Boolean = false,
)
