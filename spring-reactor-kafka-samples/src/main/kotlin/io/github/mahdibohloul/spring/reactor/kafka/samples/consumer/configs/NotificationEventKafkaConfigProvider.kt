package io.github.mahdibohloul.spring.reactor.kafka.samples.consumer.configs

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.github.mahdibohloul.spring.reactor.kafka.consumer.KafkaReceiverConfiguration
import io.github.mahdibohloul.spring.reactor.kafka.consumer.KafkaReceiverConfigurationProvider
import io.github.mahdibohloul.spring.reactor.kafka.samples.consumer.models.NotificationEvent
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kafka.receiver.ReceiverOptions

/**
 * Configuration provider for NotificationEvent Kafka consumer.
 */
@Component
class NotificationEventKafkaConfigProvider(
  @Value("\${kafka.bootstrap-servers:localhost:9092}")
  private val bootstrapServers: String,

  @Value("\${kafka.notification-events.topic:notification-events}")
  private val topic: String,

  @Value("\${kafka.notification-events.group-id:notification-events-consumer-group}")
  private val groupId: String
) : KafkaReceiverConfigurationProvider<String, NotificationEvent> {

  private val objectMapper = ObjectMapper().apply {
    registerModule(JavaTimeModule())
  }

  override fun provide(): Mono<KafkaReceiverConfiguration<String, NotificationEvent>> {
    val consumerProps = mapOf(
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
      ConsumerConfig.GROUP_ID_CONFIG to groupId,
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
      ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
      ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to "false",
      ConsumerConfig.MAX_POLL_RECORDS_CONFIG to "20"
    )

    val receiverOptions = ReceiverOptions.create<String, NotificationEvent>(consumerProps)
      .withValueDeserializer(JsonDeserializer(objectMapper))
      .subscription(listOf(topic))
      .addAssignListener { partitions ->
        println("NotificationEvent consumer assigned partitions: $partitions")
      }
      .addRevokeListener { partitions ->
        println("NotificationEvent consumer revoked partitions: $partitions")
      }

    return Mono.just(
      KafkaReceiverConfiguration(
        receiverOption = receiverOptions,
        name = "notification-events-receiver"
      )
    )
  }
}

