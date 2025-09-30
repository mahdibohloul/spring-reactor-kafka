package io.github.mahdibohloul.spring.reactor.kafka

import io.github.mahdibohloul.spring.reactor.kafka.consumer.KafkaReceiverConfiguration
import io.github.mahdibohloul.spring.reactor.kafka.consumer.KafkaReceiverConfigurationProvider
import io.github.mahdibohloul.spring.reactor.kafka.consumer.annotaitons.KafkaController
import io.github.mahdibohloul.spring.reactor.kafka.consumer.annotaitons.ReactiveKafkaListener
import reactor.core.publisher.Mono

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
}
