package io.github.mahdibohloul.spring.reactor.kafka.samples.consumer.controllers

import io.github.mahdibohloul.spring.reactor.kafka.consumer.KafkaReceiverConfiguration
import io.github.mahdibohloul.spring.reactor.kafka.consumer.annotaitons.KafkaController
import io.github.mahdibohloul.spring.reactor.kafka.consumer.annotaitons.ReactiveKafkaListener
import io.github.mahdibohloul.spring.reactor.kafka.samples.consumer.configs.OrderEventKafkaConfigProvider
import io.github.mahdibohloul.spring.reactor.kafka.samples.consumer.models.OrderEvent
import io.github.mahdibohloul.spring.reactor.kafka.samples.consumer.models.OrderEventType
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverRecord

/**
 * Kafka controller for handling OrderEvent messages.
 */
@KafkaController
class OrderEventKafkaController {

  private val logger = LoggerFactory.getLogger(OrderEventKafkaController::class.java)

  @ReactiveKafkaListener(configurationProvider = OrderEventKafkaConfigProvider::class)
  fun handleOrderEvents(
    receiver: KafkaReceiver<String, OrderEvent>,
    config: KafkaReceiverConfiguration<String, OrderEvent>,
  ): Mono<Void> {
    logger.info("Starting OrderEvent consumer with configuration: ${config.name}")

    return receiver.receive()
      .doOnNext { record ->
        logger.info("Received OrderEvent: partition=${record.partition()}, offset=${record.offset()}, key=${record.key()}")
        processOrderEvent(record)
      }
      .doOnError { error ->
        logger.error("Error processing OrderEvent", error)
      }
      .doOnComplete {
        logger.info("OrderEvent consumer completed")
      }
      .then()
  }

  private fun processOrderEvent(record: ReceiverRecord<String, OrderEvent>) {
    try {
      val orderEvent = record.value()
      logger.info("Processing OrderEvent: orderId=${orderEvent.orderId}, eventType=${orderEvent.eventType}, totalAmount=${orderEvent.totalAmount}")

      // Simulate some business logic based on event type
      when (orderEvent.eventType) {
        OrderEventType.ORDER_CREATED -> {
          logger.info("New order created: ${orderEvent.orderId} for user ${orderEvent.userId}")
          // Reserve inventory, send confirmation email, etc.
        }
        OrderEventType.ORDER_PAID -> {
          logger.info("Order paid: ${orderEvent.orderId}")
          // Process payment, update order status, trigger fulfillment, etc.
        }
        OrderEventType.ORDER_SHIPPED -> {
          logger.info("Order shipped: ${orderEvent.orderId}")
          // Send tracking information, update inventory, etc.
        }
        OrderEventType.ORDER_DELIVERED -> {
          logger.info("Order delivered: ${orderEvent.orderId}")
          // Send delivery confirmation, request feedback, etc.
        }
        OrderEventType.ORDER_CANCELLED -> {
          logger.info("Order cancelled: ${orderEvent.orderId}")
          // Release inventory, process refund, send cancellation email, etc.
        }
        OrderEventType.ORDER_REFUNDED -> {
          logger.info("Order refunded: ${orderEvent.orderId}")
          // Process refund, update financial records, etc.
        }
      }

      // Log order items for detailed processing
      orderEvent.items.forEach { item ->
        logger.debug("Order item: ${item.productName} (${item.productId}) - Qty: ${item.quantity}, Price: ${item.unitPrice}")
      }

      // Commit the offset after successful processing
      record.receiverOffset().acknowledge()
    } catch (e: Exception) {
      logger.error("Error processing OrderEvent: ${record.value()}", e)
      // In a real application, you might want to send to a dead letter queue
      // or implement retry logic here
    }
  }
}
