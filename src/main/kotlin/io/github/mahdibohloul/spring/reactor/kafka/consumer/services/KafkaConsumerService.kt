package io.github.mahdibohloul.spring.reactor.kafka.consumer.services

import java.lang.reflect.Method

/**
 * Interface defining the contract for a Kafka consumer service.
 * Provides methods to register and handle Kafka listener operations.
 */
interface KafkaConsumerService {
  /**
   * Registers a Kafka listener by validating the provided method and associating it with the given bean.
   *
   * @param method The method to be validated and registered as a Kafka listener. This method must comply with
   *        specific validation rules (e.g., annotations, parameter types) and be annotated with a supported
   *        annotation, such as `@ReactiveKafkaListener`.
   * @param bean The object (bean) containing the method to be used as a Kafka listener. This is typically the
   *        instance on which the listener method will be dynamically invoked during message consumption.
   */
  fun registerKafkaListener(method: Method, bean: Any)
}
