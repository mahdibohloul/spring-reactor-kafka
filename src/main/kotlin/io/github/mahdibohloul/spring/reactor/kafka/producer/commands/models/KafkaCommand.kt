package io.github.mahdibohloul.spring.reactor.kafka.producer.commands.models

import io.github.mahdibohloul.mediator.command.Command
import io.github.mahdibohloul.spring.reactor.kafka.producer.KafkaSenderConfigurationProvider
import io.github.mahdibohloul.spring.reactor.kafka.producer.KafkaTopic
import io.github.mahdibohloul.spring.reactor.kafka.producer.generators.KeyGenerator
import org.springframework.messaging.Message
import kotlin.reflect.KClass

data class KafkaCommand<T : Any>(
  val topic: KafkaTopic,
  val keyGeneratorBeanClass: KClass<out KeyGenerator<*>>,
  val message: Message<T>,
  val senderConfigurationProvider: KClass<out KafkaSenderConfigurationProvider<*, *>>,
) : Command
