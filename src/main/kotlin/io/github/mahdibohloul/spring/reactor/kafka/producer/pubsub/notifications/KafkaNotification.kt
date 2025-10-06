package io.github.mahdibohloul.spring.reactor.kafka.producer.pubsub.notifications

import io.github.mahdibohloul.mediator.notification.Notification
import io.github.mahdibohloul.spring.reactor.kafka.producer.KafkaSenderConfigurationProvider
import io.github.mahdibohloul.spring.reactor.kafka.producer.KafkaTopic
import io.github.mahdibohloul.spring.reactor.kafka.producer.generators.KeyGenerator
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
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
) : Notification {
  /**
   * Secondary constructor to create a `KafkaNotification` instance using a simpler signature.
   * This constructor allows specifying the Kafka topic, key generator class, message payload,
   * and Kafka sender configuration provider directly, while automatically wrapping the payload
   * in a Spring Integration `Message` object.
   *
   * @param topic The Kafka topic to which the message will be sent.
   * @param keyGeneratorClass A class reference for the key generation strategy to generate keys for Kafka messages.
   * @param message The payload of the message to be sent.
   * @param senderConfigurationProvider A class reference for a provider that supplies Kafka sender configurations.
   */
  constructor(
    topic: KafkaTopic,
    keyGeneratorClass: KClass<out KeyGenerator<*>>,
    message: T,
    senderConfigurationProvider: KClass<out KafkaSenderConfigurationProvider<*, *>>,
  ) : this(
    topic = topic,
    keyGeneratorBeanClass = keyGeneratorClass,
    message = MessageBuilder.withPayload(message).build(),
    senderConfigurationProvider = senderConfigurationProvider,
  )

  /**
   * Secondary constructor for creating a `KafkaNotification` instance with a simplified argument structure.
   *
   * This constructor facilitates the creation of a `KafkaNotification` by accepting a Kafka topic,
   * a class reference for the key generator, the message payload, optional message headers, and a class
   * reference for the Kafka sender configuration provider. It internally constructs the complete
   * `Message` object and delegates to the primary constructor.
   *
   * @param topic The Kafka topic to which the message will be sent.
   * @param keyGeneratorClass The class reference of the `KeyGenerator` implementation responsible for generating
   * the key for the Kafka message.
   * @param message The payload of the message to be sent to the Kafka topic.
   * @param headers A map containing custom headers to be added to the message. Defaults to an empty map.
   * @param senderConfigurationProvider The class reference of a `KafkaSenderConfigurationProvider` implementation
   * for configuring the Kafka producer.
   */
  constructor(
    topic: KafkaTopic,
    keyGeneratorClass: KClass<out KeyGenerator<*>>,
    message: T,
    headers: Map<String, Any> = emptyMap(),
    senderConfigurationProvider: KClass<out KafkaSenderConfigurationProvider<*, *>>,
  ) : this(
    topic = topic,
    keyGeneratorBeanClass = keyGeneratorClass,
    message = MessageBuilder.withPayload(message).copyHeaders(headers).build(),
    senderConfigurationProvider = senderConfigurationProvider,
  )
}
