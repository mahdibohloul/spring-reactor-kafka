package io.github.mahdibohloul.spring.reactor.kafka.samples.consumer.controllers

import io.github.mahdibohloul.spring.reactor.kafka.samples.consumer.models.NotificationType
import io.github.mahdibohloul.spring.reactor.kafka.samples.consumer.models.OrderEventType
import io.github.mahdibohloul.spring.reactor.kafka.samples.consumer.models.UserEventType
import io.github.mahdibohloul.spring.reactor.kafka.samples.consumer.services.KafkaProducerService
import java.time.LocalDateTime
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

/**
 * REST controller for testing Kafka message production.
 */
@RestController
@RequestMapping("/api/samples")
class SampleRestController(
  private val kafkaProducerService: KafkaProducerService
) {

  private val logger = LoggerFactory.getLogger(SampleRestController::class.java)

  @PostMapping("/user-events")
  fun sendUserEvent(@RequestBody request: SendUserEventRequest): Mono<ResponseEntity<Map<String, String>>> {
    val userEvent = kafkaProducerService.generateSampleUserEvent(request.userId, request.eventType)
    
    return kafkaProducerService.sendUserEvent(userEvent)
      .then(Mono.fromCallable {
        ResponseEntity.ok(
          mapOf(
            "message" to "UserEvent sent successfully",
            "userId" to userEvent.userId,
            "eventType" to userEvent.eventType.name,
            "timestamp" to userEvent.timestamp.toString()
          )
        )
      })
      .onErrorResume { error ->
        logger.error("Failed to send UserEvent", error)
        Mono.just(ResponseEntity.internalServerError().body(mapOf(
          "error" to "Failed to send UserEvent: ${error.message}"
        )))
      }
  }

  @PostMapping("/order-events")
  fun sendOrderEvent(@RequestBody request: SendOrderEventRequest): Mono<ResponseEntity<Map<String, String>>> {
    val orderEvent = kafkaProducerService.generateSampleOrderEvent(request.orderId, request.userId, request.eventType)
    
    return kafkaProducerService.sendOrderEvent(orderEvent)
      .then(Mono.fromCallable {
        ResponseEntity.ok(
          mapOf(
            "message" to "OrderEvent sent successfully",
            "orderId" to orderEvent.orderId,
            "userId" to orderEvent.userId,
            "eventType" to orderEvent.eventType.name,
            "totalAmount" to orderEvent.totalAmount.toString(),
            "timestamp" to orderEvent.timestamp.toString()
          )
        )
      })
      .onErrorResume { error ->
        logger.error("Failed to send OrderEvent", error)
        Mono.just(ResponseEntity.internalServerError().body(mapOf(
          "error" to "Failed to send OrderEvent: ${error.message}"
        )))
      }
  }

  @PostMapping("/notification-events")
  fun sendNotificationEvent(@RequestBody request: SendNotificationEventRequest): Mono<ResponseEntity<Map<String, String>>> {
    val notificationEvent = kafkaProducerService.generateSampleNotificationEvent(
      request.notificationId, 
      request.userId, 
      request.type
    )
    
    return kafkaProducerService.sendNotificationEvent(notificationEvent)
      .then(Mono.fromCallable {
        ResponseEntity.ok(
          mapOf(
            "message" to "NotificationEvent sent successfully",
            "notificationId" to notificationEvent.notificationId,
            "userId" to notificationEvent.userId,
            "type" to notificationEvent.type.name,
            "priority" to notificationEvent.priority.name,
            "timestamp" to notificationEvent.timestamp.toString()
          )
        )
      })
      .onErrorResume { error ->
        logger.error("Failed to send NotificationEvent", error)
        Mono.just(ResponseEntity.internalServerError().body(mapOf(
          "error" to "Failed to send NotificationEvent: ${error.message}"
        )))
      }
  }

  @PostMapping("/bulk/user-events")
  fun sendBulkUserEvents(@RequestBody request: SendBulkUserEventsRequest): Mono<ResponseEntity<Map<String, Any>>> {
    val results = mutableListOf<Map<String, String>>()
    val errors = mutableListOf<String>()
    
    return Mono.fromCallable {
      repeat(request.count) { index ->
        try {
          val userId = request.userIdPrefix + (index + 1)
          val eventType = UserEventType.entries.random()
          val userEvent = kafkaProducerService.generateSampleUserEvent(userId, eventType)
          
          kafkaProducerService.sendUserEvent(userEvent).block()
          
          results.add(mapOf(
            "userId" to userId,
            "eventType" to eventType.name,
            "status" to "sent"
          ))
        } catch (e: Exception) {
          errors.add("Failed to send event for user ${request.userIdPrefix}${index + 1}: ${e.message}")
        }
      }
      
      ResponseEntity.ok(mapOf(
        "message" to "Bulk UserEvents processing completed",
        "totalRequested" to request.count,
        "successful" to results.size,
        "failed" to errors.size,
        "results" to results,
        "errors" to errors
      ))
    }
  }

  @PostMapping("/bulk/order-events")
  fun sendBulkOrderEvents(@RequestBody request: SendBulkOrderEventsRequest): Mono<ResponseEntity<Map<String, Any>>> {
    val results = mutableListOf<Map<String, String>>()
    val errors = mutableListOf<String>()
    
    return Mono.fromCallable {
      repeat(request.count) { index ->
        try {
          val orderId = request.orderIdPrefix + (index + 1)
          val userId = request.userIdPrefix + (index + 1)
          val eventType = OrderEventType.entries.random()
          val orderEvent = kafkaProducerService.generateSampleOrderEvent(orderId, userId, eventType)
          
          kafkaProducerService.sendOrderEvent(orderEvent).block()
          
          results.add(mapOf(
            "orderId" to orderId,
            "userId" to userId,
            "eventType" to eventType.name,
            "status" to "sent"
          ))
        } catch (e: Exception) {
          errors.add("Failed to send event for order ${request.orderIdPrefix}${index + 1}: ${e.message}")
        }
      }
      
      ResponseEntity.ok(mapOf(
        "message" to "Bulk OrderEvents processing completed",
        "totalRequested" to request.count,
        "successful" to results.size,
        "failed" to errors.size,
        "results" to results,
        "errors" to errors
      ))
    }
  }

  @PostMapping("/bulk/notification-events")
  fun sendBulkNotificationEvents(@RequestBody request: SendBulkNotificationEventsRequest): Mono<ResponseEntity<Map<String, Any>>> {
    val results = mutableListOf<Map<String, String>>()
    val errors = mutableListOf<String>()
    
    return Mono.fromCallable {
      repeat(request.count) { index ->
        try {
          val notificationId = request.notificationIdPrefix + (index + 1)
          val userId = request.userIdPrefix + (index + 1)
          val type = NotificationType.entries.random()
          val notificationEvent = kafkaProducerService.generateSampleNotificationEvent(notificationId, userId, type)
          
          kafkaProducerService.sendNotificationEvent(notificationEvent).block()
          
          results.add(mapOf(
            "notificationId" to notificationId,
            "userId" to userId,
            "type" to type.name,
            "status" to "sent"
          ))
        } catch (e: Exception) {
          errors.add("Failed to send event for notification ${request.notificationIdPrefix}${index + 1}: ${e.message}")
        }
      }
      
      ResponseEntity.ok(mapOf(
        "message" to "Bulk NotificationEvents processing completed",
        "totalRequested" to request.count,
        "successful" to results.size,
        "failed" to errors.size,
        "results" to results,
        "errors" to errors
      ))
    }
  }

  @GetMapping("/health")
  fun health(): ResponseEntity<Map<String, String>> {
    return ResponseEntity.ok(mapOf(
      "status" to "UP",
      "service" to "spring-reactor-kafka-samples",
      "timestamp" to LocalDateTime.now().toString()
    ))
  }
}

// Request DTOs
data class SendUserEventRequest(
  val userId: String,
  val eventType: UserEventType
)

data class SendOrderEventRequest(
  val orderId: String,
  val userId: String,
  val eventType: OrderEventType
)

data class SendNotificationEventRequest(
  val notificationId: String,
  val userId: String,
  val type: NotificationType
)

data class SendBulkUserEventsRequest(
  val count: Int,
  val userIdPrefix: String = "user"
)

data class SendBulkOrderEventsRequest(
  val count: Int,
  val orderIdPrefix: String = "order",
  val userIdPrefix: String = "user"
)

data class SendBulkNotificationEventsRequest(
  val count: Int,
  val notificationIdPrefix: String = "notification",
  val userIdPrefix: String = "user"
)

