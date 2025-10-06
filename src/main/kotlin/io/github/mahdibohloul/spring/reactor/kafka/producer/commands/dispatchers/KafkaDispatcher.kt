package io.github.mahdibohloul.spring.reactor.kafka.producer.commands.dispatchers

import io.github.mahdibohloul.mediator.command.Command
import io.github.mahdibohloul.mediator.dispatcher.CommandDispatcher
import io.github.mahdibohloul.mediator.dispatcher.annotations.CustomCommandDispatcher
import io.github.mahdibohloul.spring.reactor.kafka.producer.ProducerException
import io.github.mahdibohloul.spring.reactor.kafka.producer.commands.models.KafkaCommand
import io.github.mahdibohloul.spring.reactor.kafka.producer.services.KafkaProducerService
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.Logger
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono

@CustomCommandDispatcher(commandTypes = [KafkaCommand::class])
class KafkaDispatcher(
  private val kafkaProducerService: KafkaProducerService,
  private val logger: Logger,
) : CommandDispatcher {
  override suspend fun sendAsync(command: Command) {
    command.toMono()
      .filter { command.canStreamOnKafka() }
      .doOnSuccess {
        if (it == null) {
          return@doOnSuccess logger.warn(
            "Command " +
              "${command::class.simpleName} is not a KafkaCommand or KafkaCommands",
          )
        }
        return@doOnSuccess logger.info("Dispatching Kafka command with type ${it::class.simpleName}")
      }.switchIfEmpty { Mono.error(ProducerException.MotSupportedCommandException(command)) }
      .flatMap {
        return@flatMap when (it) {
          is KafkaCommand<*> -> kafkaProducerService.send(
            topic = it.topic,
            keyGeneratorBeanClass = it.keyGeneratorBeanClass,
            message = it.message,
            senderConfigurationProvider = it.senderConfigurationProvider,
          )

          else -> Mono.error(ProducerException.MotSupportedCommandException(it))
        }
      }.awaitSingleOrNull()
  }

  private fun Command.canStreamOnKafka(): Boolean = when (this) {
    is KafkaCommand<*> -> true
    else -> false
  }
}
