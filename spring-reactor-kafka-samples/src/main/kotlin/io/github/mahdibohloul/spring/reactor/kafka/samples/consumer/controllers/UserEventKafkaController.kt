package io.github.mahdibohloul.spring.reactor.kafka.samples.consumer.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.github.mahdibohloul.spring.reactor.kafka.consumer.KafkaReceiverConfiguration
import io.github.mahdibohloul.spring.reactor.kafka.consumer.annotaitons.KafkaController
import io.github.mahdibohloul.spring.reactor.kafka.consumer.annotaitons.ReactiveKafkaListener
import io.github.mahdibohloul.spring.reactor.kafka.samples.consumer.configs.UserEventKafkaConfigProvider
import io.github.mahdibohloul.spring.reactor.kafka.samples.consumer.models.UserEvent
import io.github.mahdibohloul.spring.reactor.kafka.samples.consumer.models.UserEventType
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverRecord

/**
 * Kafka controller for handling UserEvent messages.
 */
@KafkaController
class UserEventKafkaController {

  private val logger = LoggerFactory.getLogger(UserEventKafkaController::class.java)

  @ReactiveKafkaListener(configurationProvider = UserEventKafkaConfigProvider::class)
  fun handleUserEvents(
    receiver: KafkaReceiver<String, UserEvent>,
    config: KafkaReceiverConfiguration<String, UserEvent>
  ): Mono<Void> {
    logger.info("Starting UserEvent consumer with configuration: ${config.name}")
    
    return receiver.receive()
      .publishOn(Schedulers.boundedElastic())
      .doOnNext { record ->
        logger.info("Received UserEvent: partition=${record.partition()}, offset=${record.offset()}, key=${record.key()}")
        processUserEvent(record)
      }
      .doOnError { error ->
        logger.error("Error processing UserEvent", error)
      }
      .doOnComplete {
        logger.info("UserEvent consumer completed")
      }
      .then()
  }

  private fun processUserEvent(record: ReceiverRecord<String, UserEvent>) {
    try {
      val userEvent = record.value()
      logger.info("Processing UserEvent: userId=${userEvent.userId}, eventType=${userEvent.eventType}, email=${userEvent.email}")
      
      // Simulate some business logic based on event type
      when (userEvent.eventType) {
        UserEventType.USER_CREATED -> {
          logger.info("New user created: ${userEvent.firstName} ${userEvent.lastName}")
          // Send welcome email, create user profile, etc.
        }
        UserEventType.USER_UPDATED -> {
          logger.info("User updated: ${userEvent.userId}")
          // Update user profile, sync with external systems, etc.
        }
        UserEventType.USER_DELETED -> {
          logger.info("User deleted: ${userEvent.userId}")
          // Cleanup user data, send notifications, etc.
        }
        UserEventType.USER_LOGIN -> {
          logger.info("User login: ${userEvent.userId}")
          // Update last login time, track login metrics, etc.
        }
        UserEventType.USER_LOGOUT -> {
          logger.info("User logout: ${userEvent.userId}")
          // Update session info, cleanup temporary data, etc.
        }
      }
      
      // Commit the offset after successful processing
      record.receiverOffset().acknowledge()
      
    } catch (e: Exception) {
      logger.error("Error processing UserEvent: ${record.value()}", e)
      // In a real application, you might want to send to a dead letter queue
      // or implement retry logic here
    }
  }
}

