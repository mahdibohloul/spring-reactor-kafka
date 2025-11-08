package io.github.mahdibohloul.spring.reactor.kafka.producer.pubsub.publishers

import io.github.mahdibohloul.mediator.notification.Notification
import io.github.mahdibohloul.spring.reactor.kafka.KafkaTestHelper
import io.github.mahdibohloul.spring.reactor.kafka.producer.KafkaMessage

object KafkaPublisherTestHelper {
  fun mockKafkaNotification(): KafkaMessage<String> = KafkaMessage.builder<String>()
    .topic(KafkaTestHelper.TestKafkaTopic.TestTopic)
    .keyGeneratorClass(KafkaTestHelper.Producer.TestKafkaKeyGenerator::class)
    .payload("test-message")
    .senderConfigurationProviderClass(KafkaTestHelper.Producer.MockKafkaSenderConfigurationProvider::class)
    .build()

  fun mockNotSupportedNotification(): Notification = object : Notification {}
}
