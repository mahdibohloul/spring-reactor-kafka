package io.github.mahdibohloul.spring.reactor.kafka.producer.pubsub.publishers

import box.tapsi.libs.utilities.logging.addTraceIdToReactorContext
import io.github.mahdibohloul.mediator.notification.Notification
import io.github.mahdibohloul.mediator.publisher.Publisher
import io.github.mahdibohloul.mediator.publisher.annotations.CustomNotificationPublisher
import io.github.mahdibohloul.spring.reactor.kafka.producer.ProducerException
import io.github.mahdibohloul.spring.reactor.kafka.producer.pubsub.notifications.KafkaNotification
import io.github.mahdibohloul.spring.reactor.kafka.producer.services.KafkaProducerService
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.Logger
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono

@CustomNotificationPublisher(notificationTypes = [KafkaNotification::class])
class KafkaPublisher(
  private val kafkaProducerService: KafkaProducerService,
  private val logger: Logger,
) : Publisher {
  override suspend fun publishAsync(notification: Notification) {
    notification.toMono()
      .filter { notification.canStreamOnKafka() }
      .doOnSuccess {
        if (it == null) {
          return@doOnSuccess logger.warn(
            "Notification " +
              "${notification::class.simpleName} is not a KafkaNotification or KafkaNotifications",
          )
        }
        return@doOnSuccess logger.info("Publishing Kafka notification with type ${it::class.simpleName}")
      }.switchIfEmpty { Mono.error(ProducerException.NotSupportedNotificationException(notification)) }
      .flatMap {
        return@flatMap when (it) {
          is KafkaNotification<*> -> kafkaProducerService.send(
            topic = it.topic,
            keyGeneratorBeanClass = it.keyGeneratorBeanClass,
            message = it.message,
            senderConfigurationProvider = it.senderConfigurationProvider,
          )

          else -> Mono.error(ProducerException.NotSupportedNotificationException(it))
        }
      }.addTraceIdToReactorContext()
      .awaitSingleOrNull()
  }

  private fun Notification.canStreamOnKafka(): Boolean = when (this) {
    is KafkaNotification<*> -> true
    else -> false
  }
}
