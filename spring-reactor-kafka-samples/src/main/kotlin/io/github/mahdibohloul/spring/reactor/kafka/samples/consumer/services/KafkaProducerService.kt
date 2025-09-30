package io.github.mahdibohloul.spring.reactor.kafka.samples.consumer.services

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.github.mahdibohloul.spring.reactor.kafka.samples.consumer.models.NotificationEvent
import io.github.mahdibohloul.spring.reactor.kafka.samples.consumer.models.NotificationPriority
import io.github.mahdibohloul.spring.reactor.kafka.samples.consumer.models.NotificationType
import io.github.mahdibohloul.spring.reactor.kafka.samples.consumer.models.OrderEvent
import io.github.mahdibohloul.spring.reactor.kafka.samples.consumer.models.OrderEventType
import io.github.mahdibohloul.spring.reactor.kafka.samples.consumer.models.OrderItem
import io.github.mahdibohloul.spring.reactor.kafka.samples.consumer.models.UserEvent
import io.github.mahdibohloul.spring.reactor.kafka.samples.consumer.models.UserEventType
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderOptions
import reactor.kafka.sender.SenderRecord
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Service for producing Kafka messages for testing purposes.
 */
@Service
class KafkaProducerService(
  @Value("\${kafka.bootstrap-servers:localhost:9092}")
  private val bootstrapServers: String,

  @Value("\${kafka.user-events.topic:user-events}")
  private val userEventsTopic: String,

  @Value("\${kafka.order-events.topic:order-events}")
  private val orderEventsTopic: String,

  @Value("\${kafka.notification-events.topic:notification-events}")
  private val notificationEventsTopic: String,
) {

  private val logger = LoggerFactory.getLogger(KafkaProducerService::class.java)
  private val objectMapper = ObjectMapper().apply {
    registerModule(JavaTimeModule())
    activateDefaultTyping(
      BasicPolymorphicTypeValidator.builder().build(),
      ObjectMapper.DefaultTyping.NON_FINAL,
      JsonTypeInfo.As.PROPERTY,
    )
  }

  private val kafkaSender: KafkaSender<String, String> by lazy {
    val producerProps = mapOf(
      ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
      ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
      ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
      ProducerConfig.ACKS_CONFIG to "all",
      ProducerConfig.RETRIES_CONFIG to "3",
      ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG to "true",
    )

    val senderOptions = SenderOptions.create<String, String>(producerProps)
    KafkaSender.create(senderOptions)
  }

  fun sendUserEvent(userEvent: UserEvent): Mono<Void> = try {
    val jsonValue = objectMapper.writeValueAsString(userEvent)
    val record = SenderRecord.create(
      userEventsTopic,
      null,
      null,
      userEvent.userId,
      jsonValue,
      null,
    )

    kafkaSender.send(Mono.just(record))
      .doOnNext {
        logger.info("UserEvent sent successfully: userId=${userEvent.userId}, eventType=${userEvent.eventType}")
      }
      .doOnError { error ->
        logger.error("Failed to send UserEvent: userId=${userEvent.userId}", error)
      }.then()
  } catch (e: Exception) {
    logger.error("Error serializing UserEvent: ${userEvent.userId}", e)
    Mono.error(e)
  }

  fun sendOrderEvent(orderEvent: OrderEvent): Mono<Void> = try {
    val jsonValue = objectMapper.writeValueAsString(orderEvent)
    val record = SenderRecord.create(
      orderEventsTopic,
      null,
      null,
      orderEvent.orderId,
      jsonValue,
      null,
    )

    kafkaSender.send(Mono.just(record))
      .doOnNext {
        logger.info("OrderEvent sent successfully: orderId=${orderEvent.orderId}, eventType=${orderEvent.eventType}")
      }
      .doOnError { error ->
        logger.error("Failed to send OrderEvent: orderId=${orderEvent.orderId}", error)
      }
      .then()
  } catch (e: Exception) {
    logger.error("Error serializing OrderEvent: ${orderEvent.orderId}", e)
    Mono.error(e)
  }

  fun sendNotificationEvent(notificationEvent: NotificationEvent): Mono<Void> = try {
    val jsonValue = objectMapper.writeValueAsString(notificationEvent)
    val record = SenderRecord.create(
      notificationEventsTopic,
      null,
      null,
      notificationEvent.notificationId,
      jsonValue,
      null,
    )

    kafkaSender.send(Mono.just(record))
      .doOnNext {
        logger.info("NotificationEvent sent successfully: notificationId=${notificationEvent.notificationId}, type=${notificationEvent.type}")
      }
      .doOnError { error ->
        logger.error("Failed to send NotificationEvent: notificationId=${notificationEvent.notificationId}", error)
      }
      .then()
  } catch (e: Exception) {
    logger.error("Error serializing NotificationEvent: ${notificationEvent.notificationId}", e)
    Mono.error(e)
  }

  fun generateSampleUserEvent(userId: String, eventType: UserEventType): UserEvent = UserEvent(
    userId = userId,
    eventType = eventType,
    email = "user$userId@example.com",
    firstName = "User$userId",
    lastName = "LastName$userId",
    timestamp = LocalDateTime.now(),
    metadata = mapOf(
      "source" to "sample-generator",
      "version" to "1.0",
      "generatedAt" to LocalDateTime.now().toString(),
    ),
  )

  fun generateSampleOrderEvent(orderId: String, userId: String, eventType: OrderEventType): OrderEvent = OrderEvent(
    orderId = orderId,
    userId = userId,
    eventType = eventType,
    totalAmount = BigDecimal.valueOf(99.99),
    currency = "USD",
    items = listOf(
      OrderItem(
        productId = "PROD-001",
        productName = "Sample Product 1",
        quantity = 2,
        unitPrice = BigDecimal.valueOf(29.99),
      ),
      OrderItem(
        productId = "PROD-002",
        productName = "Sample Product 2",
        quantity = 1,
        unitPrice = BigDecimal.valueOf(39.99),
      ),
    ),
    timestamp = LocalDateTime.now(),
    metadata = mapOf(
      "source" to "sample-generator",
      "version" to "1.0",
      "generatedAt" to LocalDateTime.now().toString(),
    ),
  )

  fun generateSampleNotificationEvent(
    notificationId: String,
    userId: String,
    type: NotificationType,
  ): NotificationEvent = NotificationEvent(
    notificationId = notificationId,
    userId = userId,
    type = type,
    title = "Sample ${type.name} Notification",
    message = "This is a sample ${type.name.lowercase()} notification for user $userId",
    priority = when (type) {
      NotificationType.EMAIL -> NotificationPriority.MEDIUM
      NotificationType.SMS -> NotificationPriority.HIGH
      NotificationType.PUSH -> NotificationPriority.MEDIUM
      NotificationType.IN_APP -> NotificationPriority.LOW
    },
    timestamp = LocalDateTime.now(),
    metadata = mapOf(
      "source" to "sample-generator",
      "version" to "1.0",
      "generatedAt" to LocalDateTime.now().toString(),
    ),
  )

  fun shutdown() {
    kafkaSender.close()
  }
}
