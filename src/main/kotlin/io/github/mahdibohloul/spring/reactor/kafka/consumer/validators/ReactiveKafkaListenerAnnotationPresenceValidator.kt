package io.github.mahdibohloul.spring.reactor.kafka.consumer.validators

import box.tapsi.libs.utilities.validator.Validator
import io.github.mahdibohloul.spring.reactor.kafka.consumer.KafkaConsumerException
import io.github.mahdibohloul.spring.reactor.kafka.consumer.annotaitons.ReactiveKafkaListener
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.lang.reflect.Method

@Component(ReactiveKafkaListenerAnnotationPresenceValidator.BEAN_NAME)
class ReactiveKafkaListenerAnnotationPresenceValidator : Validator<Method> {
  override fun validate(input: Method): Mono<Void> = Mono.fromCallable {
    AnnotationUtils.findAnnotation(input, ReactiveKafkaListener::class.java) != null
  }.handle { isPresent, sink ->
    if (isPresent) {
      return@handle sink.complete()
    }
    return@handle sink.error(
      KafkaConsumerException.KafkaConsumerInitializationException.fromAnnotationNotFound(
        ReactiveKafkaListener::class,
      ),
    )
  }

  companion object {
    const val BEAN_NAME = "reactiveKafkaListenerAnnotationPresenceValidator"
  }
}
