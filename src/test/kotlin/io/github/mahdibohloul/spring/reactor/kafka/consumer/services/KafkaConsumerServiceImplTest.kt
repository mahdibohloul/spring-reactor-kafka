package io.github.mahdibohloul.spring.reactor.kafka.consumer.services

import box.tapsi.libs.utilities.validator.factories.ValidatorFactory
import io.github.mahdibohloul.spring.reactor.kafka.KafkaTestHelper
import io.github.mahdibohloul.spring.reactor.kafka.consumer.ConsumerProperties
import io.github.mahdibohloul.spring.reactor.kafka.consumer.KafkaReceiverConfiguration
import io.github.mahdibohloul.spring.reactor.kafka.consumer.factories.KafkaReceiverFactory
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.mock
import org.springframework.context.ApplicationContext
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions
import java.time.Duration
import kotlin.reflect.jvm.javaMethod
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class KafkaConsumerServiceImplTest {
  @Mock
  private lateinit var applicationContext: ApplicationContext

  @Mock
  private lateinit var validatorFactory: ValidatorFactory

  @Mock
  private lateinit var kafkaReceiverFactory: KafkaReceiverFactory

  private lateinit var service: KafkaConsumerServiceImpl

  @BeforeEach
  fun init() {
    MockitoAnnotations.openMocks(this)
    service = serviceWith(resubscribeEnabled = true)
  }

  private fun serviceWith(resubscribeEnabled: Boolean) = KafkaConsumerServiceImpl(
    applicationContext = applicationContext,
    validatorFactory = validatorFactory,
    kafkaReceiverFactory = kafkaReceiverFactory,
    consumerProperties = ConsumerProperties(
      enabled = true,
      resubscribe = ConsumerProperties.Resubscribe(
        enabled = resubscribeEnabled,
        minBackoff = Duration.ofMillis(10),
        maxBackoff = Duration.ofMillis(50),
      ),
    ),
  )

  @Test
  fun `should re-subscribe a listener that terminated with an error`() {
    // given
    val controller = KafkaTestHelper.Consumer.FlakyKafkaController(failures = FAILURES)

    // when
    service.invokeListener(
      method = controller::handleMessage.javaMethod!!,
      bean = controller,
      kafkaReceiver = mock<KafkaReceiver<String, String>>(),
      configuration = KafkaReceiverConfiguration(ReceiverOptions.create<String, String>(), "test-receiver"),
    )

    // verify
    await().atMost(Duration.ofSeconds(5))
      .until { controller.subscriptions.get() > FAILURES }

    assertEquals(FAILURES + 1, controller.subscriptions.get())
  }

  @Test
  fun `should keep a healthy listener on a single subscription`() {
    // given
    val controller = KafkaTestHelper.Consumer.FlakyKafkaController(failures = 0)

    // when
    service.invokeListener(
      method = controller::handleMessage.javaMethod!!,
      bean = controller,
      kafkaReceiver = mock<KafkaReceiver<String, String>>(),
      configuration = KafkaReceiverConfiguration(ReceiverOptions.create<String, String>(), "test-receiver"),
    )

    // verify
    await().atMost(Duration.ofSeconds(1))
      .until { controller.subscriptions.get() >= 1 }
    Thread.sleep(SETTLE_MILLIS)

    assertTrue(
      controller.subscriptions.get() == 1,
      "a listener that never terminates must not be re-subscribed",
    )
  }

  @Test
  fun `should not re-subscribe when re-subscription is disabled`() {
    // given
    val controller = KafkaTestHelper.Consumer.FlakyKafkaController(failures = FAILURES)
    val serviceWithoutResubscription = serviceWith(resubscribeEnabled = false)

    // when
    serviceWithoutResubscription.invokeListener(
      method = controller::handleMessage.javaMethod!!,
      bean = controller,
      kafkaReceiver = mock<KafkaReceiver<String, String>>(),
      configuration = KafkaReceiverConfiguration(ReceiverOptions.create<String, String>(), "test-receiver"),
    )

    // verify
    await().atMost(Duration.ofSeconds(1))
      .until { controller.subscriptions.get() >= 1 }
    Thread.sleep(SETTLE_MILLIS)

    assertEquals(1, controller.subscriptions.get(), "the listener must stay terminated when disabled")
  }

  companion object {
    private const val FAILURES = 3
    private const val SETTLE_MILLIS = 200L
  }
}
