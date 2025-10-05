package io.github.mahdibohloul.spring.reactor.kafka.producer.pubsub.publishers

import box.tapsi.libs.utilities.fixture.FixtureHelper
import io.github.mahdibohloul.mediator.notification.Notification
import io.github.mahdibohloul.spring.reactor.kafka.KafkaTestHelper
import io.github.mahdibohloul.spring.reactor.kafka.producer.pubsub.notifications.KafkaNotification
import org.springframework.messaging.Message
import org.springframework.messaging.MessageHeaders

object KafkaPublisherTestHelper {
  private val fixture = FixtureHelper.getDefaultFixture()

  fun mockKafkaNotification(): KafkaNotification<String> = fixture<KafkaNotification<String>> {
    property(KafkaNotification<*>::message) { mockKafkaProducerMessage() }
    property(KafkaNotification<*>::keyGeneratorBeanClass) { KafkaTestHelper.Producer.TestKafkaKeyGenerator::class }
    property(KafkaNotification<*>::topic) { KafkaTestHelper.TestKafkaTopic.TestTopic }
    property(KafkaNotification<*>::senderConfigurationProvider) {
      KafkaTestHelper.Producer.MockKafkaSenderConfigurationProvider::class
    }
  }

  fun mockNotSupportedNotification(): Notification = object : Notification {}

  private fun mockKafkaProducerMessage(): Message<String> = object : Message<String> {
    override fun getPayload(): String = "test-message"

    override fun getHeaders(): MessageHeaders = MessageHeaders(null)
  }
}
