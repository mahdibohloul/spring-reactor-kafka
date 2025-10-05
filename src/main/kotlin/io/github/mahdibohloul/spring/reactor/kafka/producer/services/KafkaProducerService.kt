package io.github.mahdibohloul.spring.reactor.kafka.producer.services

import io.github.mahdibohloul.spring.reactor.kafka.producer.KafkaSenderConfigurationProvider
import io.github.mahdibohloul.spring.reactor.kafka.producer.KafkaTopic
import io.github.mahdibohloul.spring.reactor.kafka.producer.generators.KeyGenerator
import org.springframework.messaging.Message
import reactor.core.publisher.Mono
import reactor.kafka.sender.SenderResult
import kotlin.reflect.KClass

interface KafkaProducerService {
  fun <TMessage : Any> send(
    topic: KafkaTopic,
    keyGeneratorBeanClass: KClass<out KeyGenerator<*>>,
    message: Message<TMessage>,
    senderConfigurationProvider: KClass<out KafkaSenderConfigurationProvider<*, *>>,
  ): Mono<SenderResult<Void>>
}
