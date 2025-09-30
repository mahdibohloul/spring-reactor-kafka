package io.github.mahdibohloul.spring.reactor.kafka.samples.consumer.controllers

import io.github.mahdibohloul.spring.reactor.kafka.consumer.KafkaReceiverConfiguration
import io.github.mahdibohloul.spring.reactor.kafka.consumer.annotaitons.KafkaController
import io.github.mahdibohloul.spring.reactor.kafka.consumer.annotaitons.ReactiveKafkaListener
import io.github.mahdibohloul.spring.reactor.kafka.samples.consumer.configs.NotificationEventKafkaConfigProvider
import io.github.mahdibohloul.spring.reactor.kafka.samples.consumer.models.NotificationEvent
import io.github.mahdibohloul.spring.reactor.kafka.samples.consumer.models.NotificationPriority
import io.github.mahdibohloul.spring.reactor.kafka.samples.consumer.models.NotificationType
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverRecord

/**
 * Kafka controller for handling NotificationEvent messages.
 */
@KafkaController
class NotificationEventKafkaController {

  private val logger = LoggerFactory.getLogger(NotificationEventKafkaController::class.java)

  @ReactiveKafkaListener(configurationProvider = NotificationEventKafkaConfigProvider::class)
  fun handleNotificationEvents(
    receiver: KafkaReceiver<String, NotificationEvent>,
    config: KafkaReceiverConfiguration<String, NotificationEvent>,
  ): Mono<Void> {
    logger.info("Starting NotificationEvent consumer with configuration: ${config.name}")

    return receiver.receive()
      .doOnNext { record ->
        logger.info("Received NotificationEvent: partition=${record.partition()}, offset=${record.offset()}, key=${record.key()}")
        processNotificationEvent(record)
      }
      .doOnError { error ->
        logger.error("Error processing NotificationEvent", error)
      }
      .doOnComplete {
        logger.info("NotificationEvent consumer completed")
      }
      .then()
  }

  private fun processNotificationEvent(record: ReceiverRecord<String, NotificationEvent>) {
    try {
      val notificationEvent = record.value()
      logger.info("Processing NotificationEvent: notificationId=${notificationEvent.notificationId}, type=${notificationEvent.type}, priority=${notificationEvent.priority}")

      // Simulate notification processing based on type and priority
      when (notificationEvent.type) {
        NotificationType.EMAIL -> {
          logger.info("Sending email notification: ${notificationEvent.title}")
          // Send email via email service
          simulateEmailSending(notificationEvent)
        }
        NotificationType.SMS -> {
          logger.info("Sending SMS notification: ${notificationEvent.title}")
          // Send SMS via SMS service
          simulateSMSSending(notificationEvent)
        }
        NotificationType.PUSH -> {
          logger.info("Sending push notification: ${notificationEvent.title}")
          // Send push notification via push service
          simulatePushSending(notificationEvent)
        }
        NotificationType.IN_APP -> {
          logger.info("Sending in-app notification: ${notificationEvent.title}")
          // Store in-app notification in database
          simulateInAppNotification(notificationEvent)
        }
      }

      // Handle priority-based processing
      when (notificationEvent.priority) {
        NotificationPriority.URGENT -> {
          logger.warn("URGENT notification processed: ${notificationEvent.notificationId}")
        }
        NotificationPriority.HIGH -> {
          logger.info("HIGH priority notification processed: ${notificationEvent.notificationId}")
        }
        else -> {
          logger.debug("Standard priority notification processed: ${notificationEvent.notificationId}")
        }
      }

      // Commit the offset after successful processing
      record.receiverOffset().acknowledge()
    } catch (e: Exception) {
      logger.error("Error processing NotificationEvent: ${record.value()}", e)
      // In a real application, you might want to send to a dead letter queue
      // or implement retry logic here
    }
  }

  private fun simulateEmailSending(notification: NotificationEvent) {
    logger.info("📧 Email sent to user ${notification.userId}: ${notification.title}")
    logger.info("   Message: ${notification.message}")
  }

  private fun simulateSMSSending(notification: NotificationEvent) {
    logger.info("📱 SMS sent to user ${notification.userId}: ${notification.title}")
    logger.info("   Message: ${notification.message}")
  }

  private fun simulatePushSending(notification: NotificationEvent) {
    logger.info("🔔 Push notification sent to user ${notification.userId}: ${notification.title}")
    logger.info("   Message: ${notification.message}")
  }

  private fun simulateInAppNotification(notification: NotificationEvent) {
    logger.info("🔔 In-app notification stored for user ${notification.userId}: ${notification.title}")
    logger.info("   Message: ${notification.message}")
  }
}
