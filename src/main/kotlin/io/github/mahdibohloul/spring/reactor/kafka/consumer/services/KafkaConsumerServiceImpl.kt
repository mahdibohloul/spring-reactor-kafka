package io.github.mahdibohloul.spring.reactor.kafka.consumer.services

import box.tapsi.libs.utilities.validator.Validator
import box.tapsi.libs.utilities.validator.factories.ValidatorFactory
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
import java.lang.reflect.Method
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kafka.receiver.KafkaReceiver
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2

@Service
@OnKafkaConsumerEnabled
class KafkaConsumerServiceImpl(
  private val applicationContext: ApplicationContext,
  private val validatorFactory: ValidatorFactory,
  private val kafkaReceiverFactory: KafkaReceiverFactory,
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

  @Suppress("UNCHECKED_CAST")
  private fun invokeListener(
    method: Method,
    bean: Any,
    kafkaReceiver: KafkaReceiver<*, *>,
    configuration: KafkaReceiverConfiguration<*, *>
  ) {
    Mono.defer {
      method.invoke(bean, kafkaReceiver, configuration) as Mono<Void>
    }.doOnSubscribe {
      logger.info("Invoking listener method \"${method.name}\"")
    }.subscribe({}, {
      logger.error("Error while invoking method \"${method.name}\"", it)
    }, {
      logger.info("Listener \"${method.name}\" invoked with success")
    })
  }

  private fun getConfigProvider(method: Method): KafkaReceiverConfigurationProvider<*, *> {
    val annotation = requireNotNull(AnnotationUtils.findAnnotation(method, ReactiveKafkaListener::class.java)) {
      "Method \"${method.name}\" is missing the annotation \"${ReactiveKafkaListener::class.simpleName}\""
    }
    return applicationContext.getBean(annotation.configurationProvider.java)
  }

  private fun validateAndIgnore(method: Method): Mono<Method> =
    validators.validate(method).thenReturn(method)
      .doOnNext {
        logger.info("Method \"${it.name}\" validated successfully")
      }.doOnError {
        logger.error("Method \"${method.name}\" validation failed", it)
      }
      .onErrorResume(KafkaConsumerException.KafkaConsumerInitializationException::class.java) {
        return@onErrorResume Mono.empty()
      }
}
