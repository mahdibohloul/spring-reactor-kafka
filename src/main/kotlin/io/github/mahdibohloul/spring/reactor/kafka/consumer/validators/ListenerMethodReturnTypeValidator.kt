package io.github.mahdibohloul.spring.reactor.kafka.consumer.validators

import box.tapsi.libs.utilities.isReturningMono
import box.tapsi.libs.utilities.validator.Validator
import io.github.mahdibohloul.spring.reactor.kafka.consumer.KafkaConsumerException
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.lang.reflect.Method

@Component(ListenerMethodReturnTypeValidator.BEAN_NAME)
class ListenerMethodReturnTypeValidator : Validator<Method> {
  override fun validate(input: Method): Mono<Void> = input.toMono()
    .handle { method, sink ->
      if (method.isReturningMono()) {
        return@handle sink.complete()
      }

      return@handle sink.error(
        KafkaConsumerException.KafkaConsumerInitializationException.fromIncompatibleReturnType(),
      )
    }

  companion object {
    const val BEAN_NAME = "listenerMethodReturnTypeValidator"
  }
}
