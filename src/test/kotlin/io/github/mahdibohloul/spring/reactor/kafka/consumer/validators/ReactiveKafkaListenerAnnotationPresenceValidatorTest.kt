package io.github.mahdibohloul.spring.reactor.kafka.consumer.validators

import io.github.mahdibohloul.spring.reactor.kafka.KafkaTestHelper
import io.github.mahdibohloul.spring.reactor.kafka.consumer.KafkaConsumerException
import io.github.mahdibohloul.spring.reactor.kafka.consumer.annotaitons.ReactiveKafkaListener
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.MockitoAnnotations
import reactor.kotlin.test.test
import reactor.kotlin.test.verifyError
import kotlin.reflect.jvm.javaMethod

class ReactiveKafkaListenerAnnotationPresenceValidatorTest {
  @InjectMocks
  private lateinit var validator: ReactiveKafkaListenerAnnotationPresenceValidator

  @BeforeEach
  fun init() {
    MockitoAnnotations.openMocks(this)
  }

  @Test
  fun `should complete when the method has kafka listener annotation`() {
    // given

    // when

    // verify
    validator.validate(this::validMethod.javaMethod!!)
      .test()
      .verifyComplete()
  }

  @Test
  fun `should return error when the method has no kafka listener annotation`() {
    // given

    // when

    // verify

    validator.validate(this::invalidMethod.javaMethod!!)
      .test()
      .verifyError(KafkaConsumerException.KafkaConsumerInitializationException::class)
  }

  @ReactiveKafkaListener(KafkaTestHelper.Consumer.EmptyKafkaReceiverConfigurationProvider::class)
  private fun validMethod(): Unit = Unit

  private fun invalidMethod(): Unit = Unit
}
