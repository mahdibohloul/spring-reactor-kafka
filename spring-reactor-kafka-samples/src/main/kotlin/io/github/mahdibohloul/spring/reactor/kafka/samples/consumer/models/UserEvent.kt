package io.github.mahdibohloul.spring.reactor.kafka.samples.consumer.models

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

/**
 * Sample user event model for demonstrating Kafka message consumption.
 */
data class UserEvent(
  @JsonProperty("userId")
  val userId: String,

  @JsonProperty("eventType")
  val eventType: UserEventType,

  @JsonProperty("email")
  val email: String,

  @JsonProperty("firstName")
  val firstName: String,

  @JsonProperty("lastName")
  val lastName: String,

  @JsonProperty("timestamp")
  val timestamp: LocalDateTime = LocalDateTime.now(),

  @JsonProperty("metadata")
  val metadata: Map<String, Any> = emptyMap(),
)

enum class UserEventType {
  USER_CREATED,
  USER_UPDATED,
  USER_DELETED,
  USER_LOGIN,
  USER_LOGOUT,
}
