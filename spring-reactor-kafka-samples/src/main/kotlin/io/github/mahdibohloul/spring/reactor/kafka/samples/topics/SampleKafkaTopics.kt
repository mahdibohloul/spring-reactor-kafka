package io.github.mahdibohloul.spring.reactor.kafka.samples.topics

import io.github.mahdibohloul.spring.reactor.kafka.producer.KafkaTopic

enum class SampleKafkaTopics(override val topicName: String) : KafkaTopic {
  UserEvents("user-events"),
  OrderEvents("order-events"),
  NotificationEvents("notification-events"),
}
