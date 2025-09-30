package io.github.mahdibohloul.spring.reactor.kafka.consumer.discovery

import io.github.mahdibohloul.spring.reactor.kafka.KafkaTestHelper
import io.github.mahdibohloul.spring.reactor.kafka.consumer.annotaitons.KafkaController
import io.github.mahdibohloul.spring.reactor.kafka.consumer.services.KafkaConsumerService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import org.springframework.context.ApplicationContext
import kotlin.reflect.jvm.javaMethod

class KafkaConsumerDiscoveryTest {
  @InjectMocks
  private lateinit var discovery: KafkaConsumerDiscovery

  @Mock
  private lateinit var applicationContext: ApplicationContext

  @Mock
  private lateinit var kafkaConsumerService: KafkaConsumerService

  @BeforeEach
  fun init() {
    MockitoAnnotations.openMocks(this)
  }

  @Test
  fun `should successfully register kafka listeners`() {
    // given
    val testController = KafkaTestHelper.Consumer.TestKafkaController()
    val controllers = mapOf("testController" to testController)

    // when
    whenever(applicationContext.getBeansWithAnnotation(KafkaController::class.java)).thenReturn(controllers)

    // verify
    discovery.afterPropertiesSet()

    verify(kafkaConsumerService, times(1))
      .registerKafkaListener(
        eq(testController::handleMessage.javaMethod!!),
        eq(testController),
      )
  }

  @Test
  fun `should handle empty controller list`() {
    // given

    // when
    whenever(applicationContext.getBeansWithAnnotation(KafkaController::class.java)).thenReturn(emptyMap())

    // verify
    discovery.afterPropertiesSet()
    verifyNoInteractions(kafkaConsumerService)
  }

  @Test
  fun `should handle controller without listener methods`() {
    // given
    val controllerWithoutListeners = KafkaTestHelper.Consumer.TestControllerWithoutListeners()
    val controllers = mapOf("controllerWithoutListeners" to controllerWithoutListeners)
    // when
    whenever(applicationContext.getBeansWithAnnotation(KafkaController::class.java)).thenReturn(controllers)

    // verify
    discovery.afterPropertiesSet()
    verifyNoInteractions(kafkaConsumerService)
  }

  @Test
  fun `should throw exception when registration fails`() {
    // given
    val testController = KafkaTestHelper.Consumer.TestKafkaController()
    val controllers = mapOf("testController" to testController)

    // when
    whenever(applicationContext.getBeansWithAnnotation(KafkaController::class.java)).thenReturn(controllers)
    whenever(
      kafkaConsumerService.registerKafkaListener(
        any(),
        any(),
      ),
    ).thenThrow(RuntimeException("Registration failed"))

    // verify
    assertThrows<IllegalStateException> {
      discovery.afterPropertiesSet()
    }
  }
}
