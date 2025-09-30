package io.github.mahdibohloul.spring.reactor.kafka.consumer.validators

import io.github.mahdibohloul.spring.reactor.kafka.consumer.KafkaConsumerException
import io.github.mahdibohloul.spring.reactor.kafka.consumer.KafkaReceiverConfigurationProvider
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.MockitoAnnotations
import reactor.kafka.receiver.KafkaReceiver
import reactor.kotlin.test.test
import reactor.kotlin.test.verifyError
import kotlin.reflect.jvm.javaMethod

class ListenerMethodParameterTypeKafkaReceiverValidatorTest {
  @InjectMocks
  private lateinit var validator: ListenerMethodParameterTypeKafkaReceiverValidator

  @BeforeEach
  fun init() {
    MockitoAnnotations.openMocks(this)
  }

  @Test
  fun `should complete when the first method parameter types is a kafka receiver`() {
    // given

    // when

    // verify
    validator.validate(this::validMethod.javaMethod!!)
      .test()
      .verifyComplete()
  }

  @Test
  fun `should return error when the first method parameter types is not a kafka receiver`() {
    // given

    // when

    // verify

    validator.validate(this::invalidMethod.javaMethod!!)
      .test()
      .verifyError(KafkaConsumerException.KafkaConsumerInitializationException::class)
  }

  private fun validMethod(ignored: KafkaReceiver<String, String>): Unit = Unit
  private fun invalidMethod(ignored: KafkaReceiverConfigurationProvider<String, String>): Unit = Unit
}
