package io.github.mahdibohloul.spring.reactor.kafka.producer.services

import box.tapsi.libs.utilities.fixture.FixtureHelper
import io.github.mahdibohloul.spring.reactor.kafka.KafkaTestHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.spy
import org.mockito.kotlin.whenever
import org.springframework.context.ApplicationContext
import reactor.core.publisher.Mono
import reactor.kotlin.test.test
import java.time.Duration

class KafkaProducerServiceImplTest {
  @InjectMocks
  private lateinit var producerServiceImpl: KafkaProducerServiceImpl

  @Mock
  private lateinit var applicationContext: ApplicationContext

  private val fixture = FixtureHelper.getDefaultFixture()
  private val keyGenerator = KafkaTestHelper.Producer.TestKafkaKeyGenerator

  @BeforeEach
  fun init() {
    MockitoAnnotations.openMocks(this)
    whenever(applicationContext.getBean(keyGenerator::class.java)).thenReturn(keyGenerator)
  }

  @Test
  fun `should send message to kafka`() {
    // given
    val topic = KafkaTestHelper.TestKafkaTopic.TestTopic
    val message = spy(KafkaTestHelper.Producer.TestKafkaMessage(id = fixture()))
    val senderConfigurationProvider = KafkaTestHelper.Producer.MockKafkaSenderConfigurationProvider()

    // when
    whenever(applicationContext.getBean(senderConfigurationProvider::class.java))
      .thenReturn(senderConfigurationProvider)

    // verify
    producerServiceImpl.send(topic, keyGenerator::class, message, senderConfigurationProvider::class)
      .test()
      .expectNextMatches { it.exception() == null }
      .verifyComplete()

    Assertions.assertTrue {
      senderConfigurationProvider.mockProducer.history()
        .any { record -> record.topic() == topic.topicName && record.value() == message }
    }
  }

  @Test
  fun `throws error when the kafka template throws an error`() {
    // given
    val topic = KafkaTestHelper.TestKafkaTopic.TestTopic
    val message = spy(KafkaTestHelper.Producer.TestKafkaMessage(id = fixture()))
    val exception = IllegalStateException("Kafka template error")
    val senderConfigurationProvider = KafkaTestHelper.Producer.MockKafkaSenderConfigurationProvider(false)

    // when
    whenever(applicationContext.getBean(senderConfigurationProvider::class.java))
      .thenReturn(senderConfigurationProvider)

    // verify
    producerServiceImpl.send(topic, keyGenerator::class, message, senderConfigurationProvider::class)
      .zipWith(
        Mono.delay(Duration.ofSeconds(1)).map {
          senderConfigurationProvider.mockProducer.errorNext(exception)
        },
      )
      .test()
      .verifyErrorMatches {
        it.message == exception.message
      }

    Assertions.assertTrue {
      senderConfigurationProvider.mockProducer.history()
        .any { record -> record.topic() == topic.topicName && record.value() == message }
    }
  }
}
