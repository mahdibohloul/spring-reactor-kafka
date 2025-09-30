package io.github.mahdibohloul.spring.reactor.kafka.consumer.validators

import io.github.mahdibohloul.spring.reactor.kafka.consumer.KafkaConsumerException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.MockitoAnnotations
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.test.test
import reactor.kotlin.test.verifyError
import kotlin.reflect.jvm.javaMethod

class ListenerMethodReturnTypeValidatorTest {
  @InjectMocks
  private lateinit var validator: ListenerMethodReturnTypeValidator

  @BeforeEach
  fun init() {
    MockitoAnnotations.openMocks(this)
  }

  @Test
  fun `should complete empty when the method has mono return type`() {
    // given

    // when

    // verify
    validator.validate(this::validMethod.javaMethod!!)
      .test()
      .verifyComplete()
  }

  @Test
  fun `should return error when the method has flux return type`() {
    // given

    // when

    // verify
    validator.validate(this::invalidFluxMethod.javaMethod!!)
      .test()
      .verifyError(KafkaConsumerException.KafkaConsumerInitializationException::class)
  }

  @Test
  fun `should return error when the method has sync return type`() {
    // given

    // when

    // verify
    validator.validate(this::invalidSyncMethod.javaMethod!!)
      .test()
      .verifyError(KafkaConsumerException.KafkaConsumerInitializationException::class)
  }

  private fun validMethod(): Mono<Void> = Mono.empty()

  private fun invalidFluxMethod(): Flux<Void> = Flux.empty()

  private fun invalidSyncMethod(): Unit = Unit
}
