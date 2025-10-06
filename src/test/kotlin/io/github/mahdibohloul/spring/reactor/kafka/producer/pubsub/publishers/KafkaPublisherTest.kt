package io.github.mahdibohloul.spring.reactor.kafka.producer.pubsub.publishers

import io.github.mahdibohloul.spring.reactor.kafka.producer.ProducerException
import io.github.mahdibohloul.spring.reactor.kafka.producer.services.KafkaProducerService
import kotlinx.coroutines.reactor.mono
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import org.slf4j.Logger
import reactor.core.publisher.Mono
import reactor.kotlin.test.test

class KafkaPublisherTest {
  @InjectMocks
  private lateinit var publisher: KafkaPublisher

  @Mock
  private lateinit var kafkaProducerService: KafkaProducerService

  @Mock
  @Suppress("detekt.UnusedPrivateProperty")
  private lateinit var logger: Logger

  @BeforeEach
  fun init() {
    MockitoAnnotations.openMocks(this)
  }

  @Test
  fun `should throw error when the type of notification cannot stream on kafka`() {
    // given
    val notification = KafkaPublisherTestHelper.mockNotSupportedNotification()

    // when

    // verify
    mono { publisher.publishAsync(notification) }
      .test()
      .verifyError(ProducerException.NotSupportedNotificationException::class.java)

    verifyNoInteractions(kafkaProducerService)
  }

  @Test
  fun `should return complete signal when successfully publishing notification on kafka`() {
    // given
    val notification = KafkaPublisherTestHelper.mockKafkaNotification()

    // when
    whenever(
      kafkaProducerService.send(
        topic = eq(notification.topic),
        keyGeneratorBeanClass = any(),
        message = eq(notification.message),
        senderConfigurationProvider = any(),
      ),
    ).thenReturn(Mono.empty())

    // verify
    mono { publisher.publishAsync(notification) }
      .test()
      .expectNextCount(1)
      .verifyComplete()

    verify(kafkaProducerService, times(1)).send(
      topic = eq(notification.topic),
      keyGeneratorBeanClass = any(),
      message = eq(notification.message),
      senderConfigurationProvider = any(),
    )
  }
}
