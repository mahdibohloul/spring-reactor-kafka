package io.github.mahdibohloul.spring.reactor.kafka.producer.pubsub.publishers

import io.github.mahdibohloul.mediator.notification.Notification
import io.github.mahdibohloul.spring.reactor.kafka.KafkaTestHelper
import io.github.mahdibohloul.spring.reactor.kafka.producer.pubsub.notifications.KafkaNotification

object KafkaPublisherTestHelper {
  fun mockKafkaNotification(): KafkaNotification<String> = KafkaNotification(
    topic = KafkaTestHelper.TestKafkaTopic.TestTopic,
    keyGeneratorClass = KafkaTestHelper.Producer.TestKafkaKeyGenerator::class,
    message = "test-message",
    senderConfigurationProvider = KafkaTestHelper.Producer.MockKafkaSenderConfigurationProvider::class,
  )

  fun mockNotSupportedNotification(): Notification = object : Notification {}
}
