package io.github.mahdibohloul.spring.reactor.kafka.producer

import io.github.mahdibohloul.mediator.command.Command
import io.github.mahdibohloul.mediator.notification.Notification
import io.github.mahdibohloul.spring.reactor.kafka.KafkaException

sealed class ProducerException(message: String?) : KafkaException(message) {
  class NotSupportedNotificationException(
    val notification: Notification,
  ) : ProducerException("Notification ${notification::class.simpleName} is not supported for publishing")

  class MotSupportedCommandException(
    val command: Command,
  ) : ProducerException("Command ${command::class.simpleName} is not supported for producing")
}
