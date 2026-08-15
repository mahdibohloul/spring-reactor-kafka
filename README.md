# Spring Reactor Kafka

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Kotlin](https://img.shields.io/badge/kotlin-1.9.23-blue.svg)](https://kotlinlang.org)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.mahdibohloul/spring-reactor-kafka)](https://search.maven.org/artifact/io.github.mahdibohloul/spring-reactor-kafka)

A reactive, Spring Boot–friendly toolkit for building Kafka consumers and producers using Project Reactor. This library
provides annotation-driven consumer discovery, type-safe configuration providers, and seamless producer integration. It
also integrates out-of-the-box with Kpring MediatR to publish notifications or send commands to Kafka in a
coroutine-friendly way.

### 🚀 Features

- **Reactive Consumers**: Build non-blocking Kafka consumers with Reactor `KafkaReceiver`
- **Annotation-Driven**: Use `@KafkaController` and `@ReactiveKafkaListener` to register listeners automatically
- **Type-Safe Config Providers**: Provide `KafkaReceiverConfiguration` and producer `ReactiveKafkaProducerTemplate` via
  provider interfaces
- **Auto-Configuration**: Spring Boot autoconfig for consumers and producers, gated by properties
- **Producer Service**: A simple `KafkaProducerService` for sending typed messages
- **MediatR Integration**: Publish `KafkaNotification` via `KafkaPublisher` with `kpring-mediatr-starter`. Send
  `KafkaCommand` via `KafkaDispatcher` with `kpring-mediatr-starter`.
- **Validation**: Listener method signature validators for safer configuration

## 📋 Requirements

- Java 21+
- Kotlin 1.9.25+
- Spring Framework 6.2.10+
- Spring Boot 3.5.5+
- Kafka 3.x

## 🛠️ Installation

### Maven

```xml

<dependency>
    <groupId>io.github.mahdibohloul</groupId>
    <artifactId>spring-reactor-kafka</artifactId>
    <version>1.0.0-RC1</version>
</dependency>
```

If you plan to use MediatR integration for publishing notifications, also add:

```kotlin
implementation("io.github.mahdibohloul:kpring-mediatr-starter:2.0.1")
```

## ⚙️ Auto-Configuration

This library registers the following Spring Boot auto-configurations:

- `io.github.mahdibohloul.spring.reactor.kafka.autoconfigure.ReactiveKafkaAutoConfiguration`
- `io.github.mahdibohloul.spring.reactor.kafka.consumer.autoconfigure.ReactorKafkaConsumerAutoConfiguration`
- `io.github.mahdibohloul.spring.reactor.kafka.producer.autoconfigure.ReactorKafkaProducerAutoConfiguration`

Enable features via properties:

```properties
# Enable/disable consumer infrastructure
reactor.kafka.consumer.enabled=true
# Enable/disable producer infrastructure
reactor.kafka.producer.enabled=true
```

## 🧩 Modules and Key Concepts

- `@KafkaController`: Marks a class containing Kafka listener methods
- `@ReactiveKafkaListener(configurationProvider = ...)`: Marks a method as a reactive listener and links it to a
  configuration provider
- `KafkaReceiverConfigurationProvider<TKey, TValue>`: Supplies `KafkaReceiverConfiguration` used to create
  `KafkaReceiver`
- `KafkaConsumerDiscovery`: Auto-discovers `@KafkaController` beans and registers their `@ReactiveKafkaListener` methods
- `KafkaProducerService`: Sends messages to Kafka using a provided `ReactiveKafkaProducerTemplate`
- `KafkaSenderConfigurationProvider<TKey, TValue>`: Supplies a `ReactiveKafkaProducerTemplate` for producers
- `KafkaNotification<T>`: A MediatR notification representing one Kafka message
- `KafkaPublisher`: MediatR publisher that routes `KafkaNotification` to Kafka via `KafkaProducerService`

## 🧪 Consumer Usage

1) Create a receiver configuration provider:

```kotlin
@Component
class UserEventKafkaConfigProvider(
  @Value("\${kafka.bootstrap-servers:localhost:9092}")
  private val bootstrapServers: String,
) : KafkaReceiverConfigurationProvider<String, UserEvent> {

  override fun provide(): Mono<KafkaReceiverConfiguration<String, UserEvent>> = Mono.fromCallable {
    KafkaReceiverConfiguration(
      topic = KafkaTopic { "user-events" },
      keyConfiguration = KeyConfiguration(
        keyDeserializer = StringDeserializer::class.java
      ),
      valueConfiguration = ValueConfiguration(
        valueDeserializer = JsonDeserializer::class.java
      ),
      consumerConfiguration = ConsumerConfiguration(
        bootstrapServers = bootstrapServers,
        groupId = "user-events-consumer-group"
      )
    )
  }
}
```

2) Implement a Kafka controller with a reactive listener:

```kotlin
@KafkaController
class UserEventsController {

  @ReactiveKafkaListener(configurationProvider = UserEventKafkaConfigProvider::class)
  fun consume(
    receiver: KafkaReceiver<String, UserEvent>,
    config: KafkaReceiverConfiguration<String, UserEvent>
  ): Mono<Void> = receiver
    .receive()
    .doOnNext { record ->
      // process record.value()
    }
    .then()
}
```

The consumer infrastructure is activated when `reactor.kafka.consumer.enabled=true`.

## 📤 Producer Usage

1) Create a producer sender configuration provider:

```kotlin
@Component
class GeneralEventKafkaSenderConfigProvider<T : Any>(
  @Value("\${kafka.bootstrap-servers:localhost:9092}")
  private val bootstrapServers: String,
) : KafkaSenderConfigurationProvider<String, T> {
  override fun provide(): Mono<ReactiveKafkaProducerTemplate<String, T>> = Mono.fromCallable {
    val props = mapOf(
      ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
      ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
      ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java,
      ProducerConfig.ACKS_CONFIG to "all",
    )
    val options = SenderOptions.create<String, T>(props)
    ReactiveKafkaProducerTemplate(options)
  }
}
```

2) Send with `KafkaProducerService` directly:

```kotlin
@Service
class OrderProducer(
  private val producerService: KafkaProducerService,
) {
  fun send(order: OrderEvent): Mono<SenderResult<Void>> {
    val message: Message<OrderEvent> = MessageBuilder.withPayload(order).build()
    return producerService.send(
      topic = object : KafkaTopic {
        override val topicName = "order-events"
      },
      keyGeneratorBeanClass = OrderKeyGenerator::class,
      message = message,
      senderConfigurationProvider = GeneralEventKafkaSenderConfigProvider::class,
    )
  }
}
```

3) Or publish via MediatR using `KafkaNotification`:

```kotlin
@Service
class NotificationPublisher(
  private val mediator: Mediator
) {
  suspend fun publish(notification: NotificationEvent) {
    val msg = MessageBuilder.withPayload(notification).build()
    mediator.publishAsync(
      KafkaNotification(
        topic = object : KafkaTopic {
          override val topicName = "notification-events"
        },
        keyGeneratorBeanClass = NotificationKeyGenerator::class,
        message = msg,
        senderConfigurationProvider = GeneralEventKafkaSenderConfigProvider::class,
      )
    )
  }
}
```

The producer infrastructure is activated when `reactor.kafka.producer.enabled=true`.

## 🔧 Configuration Properties

- `reactor.kafka.consumer.enabled` (Boolean, default: false)
- `reactor.kafka.consumer.resubscribe.enabled` (Boolean, default: true)
- `reactor.kafka.consumer.resubscribe.min-backoff` (Duration, default: 1s)
- `reactor.kafka.consumer.resubscribe.max-backoff` (Duration, default: 30s)
- `reactor.kafka.producer.enabled` (Boolean, default: false)

A listener that terminates with an error is re-subscribed indefinitely, with exponential backoff between
`min-backoff` and `max-backoff`. The backoff controls only how quickly it comes back, never whether it does — there is
no attempt limit, because a finite budget on a long-lived listener is spent over the whole life of the subscription
rather than per record, which makes it a time bomb rather than a safety limit.

Set `resubscribe.enabled=false` to restore the previous behaviour, where a terminated listener stays terminated for the
life of the process. Be aware of what that means: the failure is only logged, the process keeps running and readiness
probes keep passing, so the listener is silently gone until someone restarts it.

You should also set standard Kafka client properties (bootstrap servers, serializers, etc.) inside your configuration
providers.

## 🧱 Validation and Safety

Listener method signatures are validated before registration:

- Presence of `@ReactiveKafkaListener`
- Correct parameter count and types (`KafkaReceiver` first, optional `KafkaReceiverConfiguration` second)
- Return type must be `Mono<Void>` or `Flux<Void>`

## 🧭 Samples

This repository includes a runnable samples module that demonstrates consumers, producers, REST endpoints, and
configuration:

- Module: `spring-reactor-kafka-samples`

Run it with:

```bash
./gradlew :spring-reactor-kafka-samples:bootRun
```

## 🔄 Related Project

This library integrates with and is inspired by Kpring MediatR:

- Kpring MediatR: [github.com/mahdibohloul/Kpring-mediatR](https://github.com/mahdibohloul/Kpring-mediatR)

## 📦 Development

```bash
./gradlew build
./gradlew test
```

## 📄 License

This project is licensed under the MIT License - see the `LICENSE` file for details.


