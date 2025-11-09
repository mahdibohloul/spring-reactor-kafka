package io.github.mahdibohloul.spring.reactor.kafka.producer

import io.github.mahdibohloul.mediator.command.Command
import io.github.mahdibohloul.mediator.notification.Notification
import io.github.mahdibohloul.spring.reactor.kafka.producer.generators.KeyGenerator
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import kotlin.reflect.KClass

/**
 * Represents a Kafka topic in the producer system.
 *
 * This interface defines the structure for encapsulating essential
 * information about a Kafka topic, such as its name, which can be used
 * by producer services to route messages to specific topics.
 */
interface KafkaTopic {
  /**
   * The name of the Kafka topic.
   *
   * This property represents the identifier for a specific Kafka topic to which
   * messages can be published or from which messages can be consumed.
   * It serves as a key component in specifying the destination for Kafka messaging operations.
   */
  val topicName: String
}

/**
 * Represents a message to be published to a Kafka topic.
 *
 * This data class encapsulates all necessary information required to send
 * a message to a Kafka topic, such as the topic details, message payload, a key generator,
 * and sender configuration provider information.
 *
 * @param T The type of the payload in the message.
 * @property topic The topic in Kafka where the message will be sent.
 * @property keyGenerator An optional key generator used to generate keys for the message.
 * @property keyGeneratorClass An optional class reference for a key generator implementation.
 * @property message The message to be sent, including its payload and headers.
 * @property senderConfigurationProviderClass A provider class responsible for Kafka sender configuration.
 */
data class KafkaMessage<T : Any>(
  val topic: KafkaTopic,
  val keyGenerator: KeyGenerator<T>? = null,
  val keyGeneratorClass: KClass<out KeyGenerator<*>>? = null,
  val message: Message<T>,
  val senderConfigurationProviderClass: KClass<out KafkaSenderConfigurationProvider<*, *>>,
) : Notification,
  Command {
  init {
    require(keyGeneratorClass != null || keyGenerator != null) {
      "Either keyGeneratorClass or keyGenerator must be provided."
    }

    require(
      (keyGeneratorClass == null) != (keyGenerator == null),
    ) {
      "Either keyGeneratorClass or keyGenerator must be provided, but not both."
    }
  }

  /**
   * A builder class for constructing instances of `KafkaMessage` with strong typing and configurable options.
   *
   * This class provides a fluent API for defining various properties required to build
   * a Kafka message, such as topic, message payload, headers, and key generation strategies.
   *
   * @param T The type of the message payload.
   */
  class Builder<T : Any> {
    private var topic: KafkaTopic? = null

    private var keyGenerator: KeyGenerator<T>? = null

    private var keyGeneratorClass: KClass<out KeyGenerator<*>>? = null

    private var message: Message<T>? = null

    private var senderConfigurationProviderClass: KClass<out KafkaSenderConfigurationProvider<*, *>>? = null

    fun topic(topic: KafkaTopic) = apply { this.topic = topic }

    fun keyGenerator(keyGenerator: KeyGenerator<T>) = apply { this.keyGenerator = keyGenerator }

    fun keyGeneratorClass(
      keyGeneratorClass: KClass<out KeyGenerator<*>>,
    ) = apply { this.keyGeneratorClass = keyGeneratorClass }

    fun message(message: Message<T>) = apply { this.message = message }

    fun payload(payload: T) = apply {
      this.message = MessageBuilder.withPayload(payload).build()
    }

    fun headers(headers: Map<String, Any>) = apply {
      val currentPayload = checkNotNull(this.message?.payload) { "Payload must be set before setting headers." }

      val messageBuilder = MessageBuilder.withPayload(currentPayload)
      headers.forEach { (key, value) ->
        messageBuilder.setHeader(key, value)
      }
      this.message = messageBuilder.build()
    }

    fun senderConfigurationProviderClass(
      senderConfigurationProviderClass: KClass<out KafkaSenderConfigurationProvider<*, *>>,
    ) = apply { this.senderConfigurationProviderClass = senderConfigurationProviderClass }

    fun build(): KafkaMessage<T> {
      requireNotNull(topic) { "Topic must be provided" }
      requireNotNull(message) { "Message must be provided" }
      requireNotNull(senderConfigurationProviderClass) { "SenderConfigurationProviderClass must be provided" }

      return KafkaMessage(
        topic = topic!!,
        keyGenerator = keyGenerator,
        keyGeneratorClass = keyGeneratorClass,
        message = message!!,
        senderConfigurationProviderClass = senderConfigurationProviderClass!!,
      )
    }
  }

  companion object {
    fun <T : Any> builder(): Builder<T> = Builder()
  }
}
