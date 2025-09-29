package io.github.mahdibohloul.spring.reactor.kafka.consumer.annotaitons

import io.github.mahdibohloul.spring.reactor.kafka.consumer.KafkaReceiverConfigurationProvider
import kotlin.reflect.KClass
import io.github.mahdibohloul.spring.reactor.kafka.consumer.KafkaReceiverConfiguration

/**
 * Marks a method as a reactive Kafka message listener in a Spring Boot application.
 *
 * Methods annotated with @ReactiveKafkaListener must be defined within a [KafkaController] class and will be
 * automatically configured to consume messages from the specified Kafka topic.
 * The method signature must follow these requirements:
 *
 * - Must return [reactor.core.publisher.Mono]<Void> or [reactor.core.publisher.Flux]<Void>
 * - Must accept [reactor.kafka.receiver.KafkaReceiver] as the first parameter
 * - Can optionally accept [KafkaReceiverConfiguration] as the second parameter
 *
 * This annotation is fully compatible with Spring AOP and CGLib proxies.
 * You can combine it with other AOP concepts and annotations like @Transactional, @Secured, or custom aspects.
 * The infrastructure supports method interception
 * and proxy-based AOP, allowing you to add cross-cutting concerns to your Kafka message handlers.
 *
 * Example usage with additional AOP features:
 * ```
 * @ReactiveKafkaListener(configurationProvider = OrderEventKafkaConfigProvider::class)
 * @Transactional
 * @Secured("ROLE_ADMIN")
 * @CustomAspect
 * fun consumeOrderEvents(
 *     receiver: KafkaReceiver<String, OrderEvent>,
 *     config: KafkaReceiverConfiguration<String, OrderEvent>
 * ): Mono<Void> {
 *     return receiver.receive()
 *         .doOnNext { record ->
 *             // Process the OrderEvent
 *         }
 *         .then()
 * }
 * ```
 *
 * @param configurationProvider The provider class that supplies the Kafka receiver configuration.
 *                             Must implement [KafkaReceiverConfigurationProvider] interface.
 *
 * @author Mahdi Bohloul
 * @see KafkaController For class-level annotation to define Kafka controllers
 * @see KafkaReceiverConfiguration For the configuration structure
 * @see KafkaReceiverConfigurationProvider For implementing custom configuration providers
 * @see reactor.kafka.receiver.KafkaReceiver For the reactive Kafka consumer API
 * @see org.springframework.aop.framework.AopContext For accessing the proxy object in advised methods
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ReactiveKafkaListener(
  val configurationProvider: KClass<out KafkaReceiverConfigurationProvider<*, *>>,
)
