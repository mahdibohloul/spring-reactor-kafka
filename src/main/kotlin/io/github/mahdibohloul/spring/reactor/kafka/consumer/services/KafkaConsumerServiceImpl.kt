package io.github.mahdibohloul.spring.reactor.kafka.consumer.services

import box.tapsi.libs.utilities.validator.Validator
import box.tapsi.libs.utilities.validator.factories.ValidatorFactory
import io.github.mahdibohloul.spring.reactor.kafka.consumer.ConsumerProperties
import io.github.mahdibohloul.spring.reactor.kafka.consumer.KafkaConsumerException
import io.github.mahdibohloul.spring.reactor.kafka.consumer.KafkaReceiverConfiguration
import io.github.mahdibohloul.spring.reactor.kafka.consumer.KafkaReceiverConfigurationProvider
import io.github.mahdibohloul.spring.reactor.kafka.consumer.annotaitons.OnKafkaConsumerEnabled
import io.github.mahdibohloul.spring.reactor.kafka.consumer.annotaitons.ReactiveKafkaListener
import io.github.mahdibohloul.spring.reactor.kafka.consumer.factories.KafkaReceiverFactory
import io.github.mahdibohloul.spring.reactor.kafka.consumer.validators.ListenerMethodParameterSizeValidator
import io.github.mahdibohloul.spring.reactor.kafka.consumer.validators.ListenerMethodParameterTypeKafkaReceiverValidator
import io.github.mahdibohloul.spring.reactor.kafka.consumer.validators.ListenerMethodReturnTypeValidator
import io.github.mahdibohloul.spring.reactor.kafka.consumer.validators.ReactiveKafkaListenerAnnotationPresenceValidator
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kafka.receiver.KafkaReceiver
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2
import reactor.util.retry.Retry
import java.lang.reflect.Method

@Service
@OnKafkaConsumerEnabled
class KafkaConsumerServiceImpl(
  private val applicationContext: ApplicationContext,
  private val validatorFactory: ValidatorFactory,
  private val kafkaReceiverFactory: KafkaReceiverFactory,
  private val consumerProperties: ConsumerProperties,
) : KafkaConsumerService {
  private val logger = LoggerFactory.getLogger(this::class.java)

  private val validators: Validator<Method> by lazy {
    validatorFactory.getValidator(
      ReactiveKafkaListenerAnnotationPresenceValidator.BEAN_NAME,
      ListenerMethodParameterSizeValidator.BEAN_NAME,
      ListenerMethodParameterTypeKafkaReceiverValidator.BEAN_NAME,
      ListenerMethodReturnTypeValidator.BEAN_NAME,
    )
  }

  override fun registerKafkaListener(method: Method, bean: Any) {
    validateAndIgnore(method)
      .map(::getConfigProvider)
      .flatMap { configProvider -> configProvider.provide() }
      .zipWhen { config -> kafkaReceiverFactory.getKafkaReceiver(config).toMono() }
      .doOnNext { (config, kafkaReceiver) -> invokeListener(method, bean, kafkaReceiver, config) }
      .subscribe({}, {
        logger.error("Error while subscribing method ${method.name}", it)
      }, {
        logger.info("Method \"${method.name}\" subscribed to kafka receiver successfully")
      })
  }

  /**
   * Subscribes the listener [method] and keeps it subscribed.
   *
   * The listener [Mono] is long-lived: it is subscribed once here and normally never terminates.
   * Previously a terminal error was only logged, which left the listener permanently dead inside a
   * process that carried on reporting itself healthy — the consumer group silently lost a member and
   * nothing ever brought it back short of a restart. A terminated listener is therefore re-subscribed
   * indefinitely, with exponential backoff from [ConsumerProperties.Resubscribe], unless
   * re-subscription is explicitly disabled.
   *
   * Re-subscription is safe on the same [KafkaReceiver] instance: `DefaultKafkaReceiver` builds its
   * `ConsumerHandler` inside a `Flux.usingWhen` resource supplier, so every subscription creates a
   * fresh `KafkaConsumer` and releases it on termination.
   */
  @Suppress("UNCHECKED_CAST")
  internal fun invokeListener(
    method: Method,
    bean: Any,
    kafkaReceiver: KafkaReceiver<*, *>,
    configuration: KafkaReceiverConfiguration<*, *>,
  ) {
    val resubscribeEnabled = consumerProperties.resubscribe.enabled
    Mono.defer {
      method.invoke(bean, kafkaReceiver, configuration) as Mono<Void>
    }.doOnSubscribe {
      logger.info("Invoking listener method \"${method.name}\"")
    }.doOnError {
      if (resubscribeEnabled) {
        logger.error("Listener \"${method.name}\" terminated with an error; it will be re-subscribed", it)
      }
    }.let { listener ->
      if (resubscribeEnabled) listener.retryWhen(resubscribeSpec()) else listener
    }.subscribe({}, {
      logger.error(
        "Error while invoking method \"${method.name}\"; it is no longer consuming and will not be re-subscribed",
        it,
      )
    }, {
      logger.warn("Listener \"${method.name}\" completed and is no longer consuming")
    })
  }

  /**
   * An unbounded exponential backoff: a listener that stopped consuming is never left stopped, so the
   * backoff governs only how quickly it returns.
   */
  private fun resubscribeSpec(): Retry = Retry
    .backoff(Long.MAX_VALUE, consumerProperties.resubscribe.minBackoff)
    .maxBackoff(consumerProperties.resubscribe.maxBackoff)

  private fun getConfigProvider(method: Method): KafkaReceiverConfigurationProvider<*, *> {
    val annotation = requireNotNull(AnnotationUtils.findAnnotation(method, ReactiveKafkaListener::class.java)) {
      "Method \"${method.name}\" is missing the annotation \"${ReactiveKafkaListener::class.simpleName}\""
    }
    return applicationContext.getBean(annotation.configurationProvider.java)
  }

  private fun validateAndIgnore(method: Method): Mono<Method> = validators.validate(method).thenReturn(method)
    .doOnNext {
      logger.info("Method \"${it.name}\" validated successfully")
    }.doOnError {
      logger.error("Method \"${method.name}\" validation failed", it)
    }
    .onErrorResume(KafkaConsumerException.KafkaConsumerInitializationException::class.java) {
      return@onErrorResume Mono.empty()
    }
}
