package io.github.mahdibohloul.spring.reactor.kafka.samples.consumer.models

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Sample order event model for demonstrating Kafka message consumption.
 */
data class OrderEvent(
  @JsonProperty("orderId")
  val orderId: String,

  @JsonProperty("userId")
  val userId: String,

  @JsonProperty("eventType")
  val eventType: OrderEventType,

  @JsonProperty("totalAmount")
  val totalAmount: BigDecimal,

  @JsonProperty("currency")
  val currency: String = "USD",

  @JsonProperty("items")
  val items: List<OrderItem>,

  @JsonProperty("timestamp")
  val timestamp: LocalDateTime = LocalDateTime.now(),

  @JsonProperty("metadata")
  val metadata: Map<String, Any> = emptyMap()
)

data class OrderItem(
  @JsonProperty("productId")
  val productId: String,
  
  @JsonProperty("productName")
  val productName: String,
  
  @JsonProperty("quantity")
  val quantity: Int,
  
  @JsonProperty("unitPrice")
  val unitPrice: BigDecimal
)

enum class OrderEventType {
  ORDER_CREATED,
  ORDER_PAID,
  ORDER_SHIPPED,
  ORDER_DELIVERED,
  ORDER_CANCELLED,
  ORDER_REFUNDED
}

