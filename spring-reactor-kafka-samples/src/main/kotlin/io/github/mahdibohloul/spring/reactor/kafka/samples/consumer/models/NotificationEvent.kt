package io.github.mahdibohloul.spring.reactor.kafka.samples.consumer.models

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

/**
 * Sample notification event model for demonstrating Kafka message consumption.
 */
data class NotificationEvent(
  @JsonProperty("notificationId")
  val notificationId: String,

  @JsonProperty("userId")
  val userId: String,

  @JsonProperty("type")
  val type: NotificationType,

  @JsonProperty("title")
  val title: String,

  @JsonProperty("message")
  val message: String,

  @JsonProperty("priority")
  val priority: NotificationPriority = NotificationPriority.MEDIUM,

  @JsonProperty("timestamp")
  val timestamp: LocalDateTime = LocalDateTime.now(),

  @JsonProperty("metadata")
  val metadata: Map<String, Any> = emptyMap(),
)

enum class NotificationType {
  EMAIL,
  SMS,
  PUSH,
  IN_APP,
}

enum class NotificationPriority {
  LOW,
  MEDIUM,
  HIGH,
  URGENT,
}
