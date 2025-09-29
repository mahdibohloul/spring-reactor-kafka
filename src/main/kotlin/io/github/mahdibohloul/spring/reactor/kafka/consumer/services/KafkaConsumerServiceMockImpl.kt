package io.github.mahdibohloul.spring.reactor.kafka.consumer.services

import box.tapsi.libs.utilities.getOriginalClass
import box.tapsi.libs.utilities.validator.Validator
import box.tapsi.libs.utilities.validator.factories.ValidatorFactory
import io.github.mahdibohloul.spring.reactor.kafka.consumer.validators.ListenerMethodParameterSizeValidator
import io.github.mahdibohloul.spring.reactor.kafka.consumer.validators.ListenerMethodParameterTypeKafkaReceiverValidator
import io.github.mahdibohloul.spring.reactor.kafka.consumer.validators.ListenerMethodReturnTypeValidator
import io.github.mahdibohloul.spring.reactor.kafka.consumer.validators.ReactiveKafkaListenerAnnotationPresenceValidator
import java.lang.reflect.Method
import org.slf4j.Logger
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.stereotype.Service

@Service
@ConditionalOnMissingBean(KafkaConsumerServiceImpl::class)
class KafkaConsumerServiceMockImpl(
  private val logger: Logger,
  private val validatorFactory: ValidatorFactory,
) : KafkaConsumerService {
  private val validators: Validator<Method> by lazy {
    validatorFactory.getValidator(
      ReactiveKafkaListenerAnnotationPresenceValidator.BEAN_NAME,
      ListenerMethodParameterSizeValidator.BEAN_NAME,
      ListenerMethodParameterTypeKafkaReceiverValidator.BEAN_NAME,
      ListenerMethodReturnTypeValidator.BEAN_NAME,
    )
  }

  override fun registerKafkaListener(method: Method, bean: Any) {
    validators.validate(method).blockOptional()
    logger.info(
      "Kafka listener registered for method \"${method.name}\" for bean ${bean::class.getOriginalClass().simpleName} " +
        "using mock implementation",
    )
  }
}
