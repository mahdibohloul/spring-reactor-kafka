package io.github.mahdibohloul.spring.reactor.kafka.consumer.validators

import box.tapsi.libs.utilities.validator.Validator
import io.github.mahdibohloul.spring.reactor.kafka.consumer.KafkaConsumerException
import java.lang.reflect.Method
import kotlin.reflect.full.isSuperclassOf
import org.springframework.core.ResolvableType
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kafka.receiver.KafkaReceiver
import reactor.kotlin.core.publisher.toMono

@Component(ListenerMethodParameterTypeKafkaReceiverValidator.BEAN_NAME)
class ListenerMethodParameterTypeKafkaReceiverValidator : Validator<Method> {
  override fun validate(input: Method): Mono<Void> = input.toMono()
    .handle { method, sink ->
      if (KafkaReceiver::class.isSuperclassOf(
          ResolvableType.forMethodParameter(method, 0)
            .rawClass!!.kotlin,
        )
      ) {
        return@handle sink.complete()
      }

      return@handle sink.error(
        KafkaConsumerException.KafkaConsumerInitializationException.fromIncompatibleParameterType(),
      )
    }

  companion object {
    const val BEAN_NAME = "listenerMethodParameterTypeKafkaReceiverValidator"
  }
}
