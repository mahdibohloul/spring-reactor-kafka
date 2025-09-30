package io.github.mahdibohloul.spring.reactor.kafka.consumer

import org.springframework.core.ResolvableType

val KafkaReceiverConfiguration<*, *>.topics: List<String>
  get() = receiverOption.subscriptionTopics()?.toList() ?: emptyList()

val KafkaReceiverConfiguration<*, *>.keyType: Class<*>
  get() = ResolvableType.forClass(this::class.java)
    .`as`(KafkaReceiverConfiguration::class.java).getGeneric(0).toClass()

val KafkaReceiverConfiguration<*, *>.valueType: Class<*>
  get() = ResolvableType.forClass(this::class.java)
    .`as`(KafkaReceiverConfiguration::class.java).getGeneric(1).toClass()
