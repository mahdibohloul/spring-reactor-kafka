package io.github.mahdibohloul.spring.reactor.kafka.samples.consumer.configs

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.github.mahdibohloul.spring.reactor.kafka.consumer.KafkaReceiverConfiguration
import io.github.mahdibohloul.spring.reactor.kafka.consumer.KafkaReceiverConfigurationProvider
import io.github.mahdibohloul.spring.reactor.kafka.samples.consumer.models.UserEvent
import io.github.mahdibohloul.spring.reactor.kafka.samples.topics.SampleKafkaTopics
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kafka.receiver.ReceiverOptions
import java.util.Collections

/**
 * Configuration provider for UserEvent Kafka consumer.
 */
@Component
class UserEventKafkaConfigProvider(
  @Value("\${kafka.bootstrap-servers:localhost:9092}")
  private val bootstrapServers: String,

  @Value("\${kafka.user-events.group-id:user-events-consumer-group}")
  private val groupId: String,
) : KafkaReceiverConfigurationProvider<String, UserEvent> {

  private val objectMapper = ObjectMapper().apply {
    registerModule(JavaTimeModule())
  }

  override fun provide(): Mono<KafkaReceiverConfiguration<String, UserEvent>> {
    val consumerProps = mapOf(
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
      ConsumerConfig.GROUP_ID_CONFIG to groupId,
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
      ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
      ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to "false",
      ConsumerConfig.MAX_POLL_RECORDS_CONFIG to "10",
    )

    val receiverOptions = ReceiverOptions.create<String, UserEvent>(consumerProps)
      .withValueDeserializer(JsonDeserializer(UserEvent::class.java, objectMapper))
      .subscription(Collections.singleton(SampleKafkaTopics.UserEvents.topicName))
      .addAssignListener { partitions ->
        println("UserEvent consumer assigned partitions: $partitions")
      }
      .addRevokeListener { partitions ->
        println("UserEvent consumer revoked partitions: $partitions")
      }

    return Mono.just(
      KafkaReceiverConfiguration(
        receiverOption = receiverOptions,
        name = "user-events-receiver",
      ),
    )
  }
}
