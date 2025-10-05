package io.github.mahdibohloul.spring.reactor.kafka.producer.generators

import io.github.mahdibohloul.spring.reactor.kafka.producer.KafkaTopic
import org.springframework.messaging.Message

fun interface KeyGenerator<TValue : Any> {
  fun generateKey(topic: KafkaTopic, message: Message<TValue>): String
}
