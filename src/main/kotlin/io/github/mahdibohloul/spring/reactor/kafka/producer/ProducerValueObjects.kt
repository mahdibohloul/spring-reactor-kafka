package io.github.mahdibohloul.spring.reactor.kafka.producer

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
