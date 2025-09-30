package io.github.mahdibohloul.spring.reactor.kafka.consumer.annotaitons

import org.springframework.core.annotation.AliasFor
import org.springframework.stereotype.Controller

/**
 * Marks a class as a Kafka controller for handling Kafka message consumption.
 *
 * Classes annotated with @KafkaController are automatically detected and configured by the
 * [tapsi.delivery.infrastructure.kafka.consumer.autoconfig.KafkaConsumerAutoConfiguration].
 * Within these controller classes, methods annotated with [ReactiveKafkaListener] serve as
 * message handlers and will be automatically connected to configured Kafka receivers.
 *
 * Usage example:
 * ```
 * @KafkaController
 * class MyKafkaController {
 *     @ReactiveKafkaListener(configurationProvider = MyKafkaReceiverConfigurationProvider::class)
 *     fun handleMessage(
 *     receiver: KafkaReceiver<String, MyMessageType>,
 *     config: KafkaReceiverConfiguration<String, MyMessageType>,
 *     ): Mono<Void> {
 *         // Handle Kafka message
 *     }
 * }
 * ```
 *
 * @author Mahdi Bohloul
 * @see ReactiveKafkaListener For method-level annotation to define Kafka message handlers
 * @see tapsi.delivery.infrastructure.kafka.consumer.autoconfig.KafkaConsumerAutoConfiguration
 * For autoconfiguration details
 * @see tapsi.delivery.infrastructure.kafka.consumer.KafkaReceiverConfiguration For Kafka receiver configuration
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Controller
annotation class KafkaController(
  @get:AliasFor(annotation = Controller::class)
  val value: String = "",
)
