package io.github.mahdibohloul.spring.reactor.kafka.consumer

import io.github.mahdibohloul.spring.reactor.kafka.KafkaException
import kotlin.reflect.KClass

sealed class KafkaConsumerException(message: String?) : KafkaException(message) {
  class KafkaConsumerInitializationException private constructor(message: String?) : KafkaConsumerException(message) {
    companion object {
      fun fromAnnotationNotFound(annotation: KClass<out Annotation>): KafkaConsumerInitializationException =
        KafkaConsumerInitializationException("Annotation $annotation not found")

      fun fromIncompatibleParameterSize(): KafkaConsumerInitializationException =
        KafkaConsumerInitializationException("Method must have one argument of type KafkaReceiver")

      fun fromIncompatibleParameterType(): KafkaConsumerInitializationException =
        KafkaConsumerInitializationException("Method must have one argument of type KafkaReceiver")

      fun fromIncompatibleReturnType(): KafkaConsumerInitializationException =
        KafkaConsumerInitializationException("Method must return Mono<Void>")
    }
  }
}
