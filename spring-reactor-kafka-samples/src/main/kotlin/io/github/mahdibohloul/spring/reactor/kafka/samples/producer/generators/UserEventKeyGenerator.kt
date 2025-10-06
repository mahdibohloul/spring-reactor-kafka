package io.github.mahdibohloul.spring.reactor.kafka.samples.producer.generators

import io.github.mahdibohloul.spring.reactor.kafka.producer.KafkaTopic
import io.github.mahdibohloul.spring.reactor.kafka.producer.generators.KeyGenerator
import io.github.mahdibohloul.spring.reactor.kafka.samples.consumer.models.UserEvent
import org.springframework.messaging.Message
import org.springframework.stereotype.Component

@Component
class UserEventKeyGenerator : KeyGenerator<UserEvent> {
  override fun generateKey(
    topic: KafkaTopic,
    message: Message<UserEvent>,
  ): String = message.payload.userId
}
