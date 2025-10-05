package io.github.mahdibohloul.spring.reactor.kafka.producer.pubsub.notifications

import io.github.mahdibohloul.mediator.notification.Notification
import io.github.mahdibohloul.spring.reactor.kafka.producer.KafkaSenderConfigurationProvider
import io.github.mahdibohloul.spring.reactor.kafka.producer.KafkaTopic
import io.github.mahdibohloul.spring.reactor.kafka.producer.generators.KeyGenerator
import org.springframework.messaging.Message
import kotlin.reflect.KClass

/**
 * Represents a notification for producing a single message to a specific Kafka topic.
 * The notification encapsulates the topic details, key generation strategy, message payload,
 * and a provider for Kafka sender configuration.
 *
 * @param T The type of the message payload.
 * @property topic The Kafka topic where the message will be published.
 * @property keyGeneratorBeanClass A class reference for the key generation strategy,
 * which is responsible for generating keys for the Kafka messages.
 * @property message The message payload to be sent to the specified Kafka topic.
 * @property senderConfigurationProvider A provider that supplies configuration for creating
 * reactive Kafka producer templates used in publishing the message.
 */
data class KafkaNotification<T : Any>(
  val topic: KafkaTopic,
  val keyGeneratorBeanClass: KClass<out KeyGenerator<*>>,
  val message: Message<T>,
  val senderConfigurationProvider: KClass<out KafkaSenderConfigurationProvider<*, *>>,
) : Notification
