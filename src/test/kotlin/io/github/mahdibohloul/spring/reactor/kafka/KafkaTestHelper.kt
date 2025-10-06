package io.github.mahdibohloul.spring.reactor.kafka

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.github.mahdibohloul.spring.reactor.kafka.consumer.KafkaReceiverConfiguration
import io.github.mahdibohloul.spring.reactor.kafka.consumer.KafkaReceiverConfigurationProvider
import io.github.mahdibohloul.spring.reactor.kafka.consumer.annotaitons.KafkaController
import io.github.mahdibohloul.spring.reactor.kafka.consumer.annotaitons.ReactiveKafkaListener
import io.github.mahdibohloul.spring.reactor.kafka.producer.KafkaSenderConfigurationProvider
import io.github.mahdibohloul.spring.reactor.kafka.producer.KafkaTopic
import io.github.mahdibohloul.spring.reactor.kafka.producer.generators.KeyGenerator
import org.apache.kafka.clients.producer.MockProducer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.kafka.support.JacksonUtils
import org.springframework.kafka.support.serializer.JsonSerializer
import org.springframework.messaging.Message
import org.springframework.messaging.MessageHeaders
import reactor.core.publisher.Mono
import reactor.kafka.sender.SenderOptions
import reactor.kafka.sender.internals.DefaultKafkaSender
import reactor.kafka.sender.internals.ProducerFactory
import kotlin.random.Random
import org.apache.kafka.clients.producer.Producer as KafkaClientsProducerProducer

object KafkaTestHelper {
  object Consumer {
    object EmptyKafkaReceiverConfigurationProvider : KafkaReceiverConfigurationProvider<String, String> {
      override fun provide(): Mono<KafkaReceiverConfiguration<String, String>> = Mono.empty()
    }

    @KafkaController
    class TestKafkaController {
      @ReactiveKafkaListener(EmptyKafkaReceiverConfigurationProvider::class)
      fun handleMessage(ignored: String) {
        // Test implementation
      }
    }

    @KafkaController
    class TestControllerWithoutListeners {
      fun someMethod() {
        // Test implementation
      }
    }
  }

  object Producer {
    class MockKafkaSenderConfigurationProvider(
      autoComplete: Boolean = true,
    ) : KafkaSenderConfigurationProvider<String, TestKafkaMessage> {
      val mockProducer = MockProducer<String, TestKafkaMessage>(
        autoComplete,
        StringSerializer(),
        JsonSerializer(JacksonUtils.enhancedObjectMapper().registerKotlinModule()),
      )

      override fun provide(): Mono<ReactiveKafkaProducerTemplate<String, TestKafkaMessage>> = Mono.fromCallable {
        ReactiveKafkaProducerTemplate(
          DefaultKafkaSender(
            object : ProducerFactory() {
              override fun <K : Any, V : Any> createProducer(
                senderOptions: SenderOptions<K, V>,
              ): KafkaClientsProducerProducer<K, V> = mockProducer as KafkaClientsProducerProducer<K, V>
            },
            SenderOptions.create(),
          ),
        )
      }
    }

    object TestKafkaKeyGenerator : KeyGenerator<String> {
      override fun generateKey(topic: KafkaTopic, message: Message<String>): String = Random.nextLong().toString()
    }

    data class TestKafkaMessage(
      val id: String,
    ) : Message<TestKafkaMessage> {
      @JsonIgnore
      override fun getPayload(): TestKafkaMessage = this

      @JsonIgnore
      override fun getHeaders(): MessageHeaders = MessageHeaders(null)
    }
  }

  enum class TestKafkaTopic(override val topicName: String) : KafkaTopic {
    TestTopic("test-topic"),
  }
}
