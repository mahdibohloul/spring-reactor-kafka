package io.github.mahdibohloul.spring.reactor.kafka.samples.producer.services

import io.github.mahdibohloul.mediator.Mediator
import io.github.mahdibohloul.spring.reactor.kafka.producer.pubsub.notifications.KafkaNotification
import io.github.mahdibohloul.spring.reactor.kafka.samples.consumer.models.NotificationEvent
import io.github.mahdibohloul.spring.reactor.kafka.samples.consumer.models.NotificationPriority
import io.github.mahdibohloul.spring.reactor.kafka.samples.consumer.models.NotificationType
import io.github.mahdibohloul.spring.reactor.kafka.samples.consumer.models.OrderEvent
import io.github.mahdibohloul.spring.reactor.kafka.samples.consumer.models.OrderEventType
import io.github.mahdibohloul.spring.reactor.kafka.samples.consumer.models.OrderItem
import io.github.mahdibohloul.spring.reactor.kafka.samples.consumer.models.UserEvent
import io.github.mahdibohloul.spring.reactor.kafka.samples.consumer.models.UserEventType
import io.github.mahdibohloul.spring.reactor.kafka.samples.producer.configs.GeneralEventKafkaSenderConfigProvider
import io.github.mahdibohloul.spring.reactor.kafka.samples.producer.generators.NotificationEventKeyGenerator
import io.github.mahdibohloul.spring.reactor.kafka.samples.producer.generators.OrderEventKeyGenerator
import io.github.mahdibohloul.spring.reactor.kafka.samples.producer.generators.UserEventKeyGenerator
import io.github.mahdibohloul.spring.reactor.kafka.samples.topics.SampleKafkaTopics
import kotlinx.coroutines.reactor.mono
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Service for producing Kafka messages for testing purposes.
 */
@Service("sampleKafkaProducerService")
class SampleKafkaProducerService(
  private val mediator: Mediator,
) {
  fun sendUserEvent(userEvent: UserEvent): Mono<Void> = mono {
    mediator.publishAsync(
      KafkaNotification(
        topic = SampleKafkaTopics.UserEvents,
        keyGeneratorBeanClass = UserEventKeyGenerator::class,
        message = MessageBuilder.withPayload(userEvent).build(),
        senderConfigurationProvider = GeneralEventKafkaSenderConfigProvider::class,
      ),
    )
  }.then()

  fun sendOrderEvent(orderEvent: OrderEvent): Mono<Void> = mono {
    mediator.publishAsync(
      KafkaNotification(
        topic = SampleKafkaTopics.OrderEvents,
        keyGeneratorBeanClass = OrderEventKeyGenerator::class,
        message = MessageBuilder.withPayload(orderEvent).build(),
        senderConfigurationProvider = GeneralEventKafkaSenderConfigProvider::class,
      ),
    )
  }.then()

  fun sendNotificationEvent(notificationEvent: NotificationEvent): Mono<Void> = mono {
    mediator.publishAsync(
      KafkaNotification(
        topic = SampleKafkaTopics.NotificationEvents,
        keyGeneratorBeanClass = NotificationEventKeyGenerator::class,
        message = MessageBuilder.withPayload(notificationEvent).build(),
        senderConfigurationProvider = GeneralEventKafkaSenderConfigProvider::class,
      ),
    )
  }.then()

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
}
