package io.github.mahdibohloul.spring.reactor.kafka.consumer.services

import java.lang.reflect.Method

interface KafkaConsumerService {
  fun registerKafkaListener(method: Method, bean: Any)
}
