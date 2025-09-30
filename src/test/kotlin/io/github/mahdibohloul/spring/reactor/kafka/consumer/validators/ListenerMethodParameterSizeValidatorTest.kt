package io.github.mahdibohloul.spring.reactor.kafka.consumer.validators

import io.github.mahdibohloul.spring.reactor.kafka.consumer.KafkaConsumerException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.MockitoAnnotations
import reactor.kotlin.test.test
import reactor.kotlin.test.verifyError
import kotlin.reflect.jvm.javaMethod

class ListenerMethodParameterSizeValidatorTest {
  @InjectMocks
  private lateinit var validator: ListenerMethodParameterSizeValidator

  @BeforeEach
  fun init() {
    MockitoAnnotations.openMocks(this)
  }

  @Test
  fun `should complete when method has one parameter`() {
    // given

    // when

    // verify
    validator.validate(this::validMethod.javaMethod!!)
      .test()
      .verifyComplete()
  }

  @Test
  fun `should return error when method has not two parameter`() {
    // given

    // when

    // verify
    validator.validate(this::invalidMethod.javaMethod!!)
      .test()
      .verifyError(KafkaConsumerException.KafkaConsumerInitializationException::class)
  }

  private fun validMethod(ignored: String, expected: String): Unit = Unit

  private fun invalidMethod(ignored: String): Unit = Unit
}
