package io.github.mahdibohloul.spring.reactor.kafka.consumer

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

/**
 * Configuration properties for Reactor Kafka consumer settings.
 *
 * This data class is used for defining and accessing the properties
 * related to Reactor Kafka consumers within a Spring Boot application.
 * It allows enabling or disabling the consumer infrastructure
 * and listener discovery.
 *
 * This configuration can be adjusted via the `reactor.kafka.consumer`
 * property namespace in application properties or YAML files.
 *
 * Property reference:
 * - `reactor.kafka.consumer.enabled`: Specifies whether the consumer
 *   infrastructure and auto-discovery of `@KafkaController` listeners
 *   are enabled.
 *   Default is `false`.
 * - `reactor.kafka.consumer.resubscribe.min-backoff` /
 *   `reactor.kafka.consumer.resubscribe.max-backoff`: The exponential backoff
 *   applied before a terminated listener is re-subscribed.
 *   Defaults are `1s` and `30s`.
 *
 * Used in conjunction with `ReactorKafkaConsumerAutoConfiguration`
 * to setup and manage Kafka consumers.
 *
 * @see io.github.mahdibohloul.spring.reactor.kafka.producer.autoconfigure.ReactorKafkaProducerAutoConfiguration
 * @see io.github.mahdibohloul.spring.reactor.kafka.consumer.discovery.KafkaConsumerDiscovery
 * @see io.github.mahdibohloul.spring.reactor.kafka.consumer.annotaitons.KafkaController
 */
@ConfigurationProperties("reactor.kafka.consumer")
data class ConsumerProperties(
  /** Whether to enable consumer auto-configuration and listener discovery. */
  val enabled: Boolean = false,
  /** Backoff applied before re-subscribing a listener that terminated. */
  val resubscribe: Resubscribe = Resubscribe(),
) {
  /**
   * Whether, and how quickly, a terminated listener is re-subscribed.
   *
   * When [enabled] — the default — re-subscription is *unbounded*, and [minBackoff]/[maxBackoff]
   * control only how fast the listener comes back, never whether it does. That asymmetry is
   * deliberate: a finite attempt budget on a long-lived listener looks like a safety limit and
   * behaves like a time bomb, because the budget is spent over the whole life of the subscription
   * rather than per record. There is therefore no `maxAttempts` here — the choice is self-healing
   * or not, with nothing in between that looks safe and is not.
   *
   * Set [enabled] to `false` to restore the previous behaviour, where a listener that terminates
   * stays terminated for the life of the process. Note what that means in practice: the failure is
   * only logged, the process keeps running, and readiness probes keep passing, so the listener is
   * silently gone until someone restarts it.
   *
   * [minBackoff] also gives the previous subscription's consumer time to close before a new one is
   * created for the same receiver.
   *
   * @property enabled Whether a terminated listener is re-subscribed at all.
   * @property minBackoff The delay before the first re-subscription attempt.
   * @property maxBackoff The ceiling the exponential backoff grows to.
   */
  data class Resubscribe(
    val enabled: Boolean = true,
    val minBackoff: Duration = DEFAULT_MIN_BACKOFF,
    val maxBackoff: Duration = DEFAULT_MAX_BACKOFF,
  ) {
    companion object {
      val DEFAULT_MIN_BACKOFF: Duration = Duration.ofSeconds(1)
      val DEFAULT_MAX_BACKOFF: Duration = Duration.ofSeconds(30)
    }
  }
}
