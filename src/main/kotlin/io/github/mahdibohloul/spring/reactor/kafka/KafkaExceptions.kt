package io.github.mahdibohloul.spring.reactor.kafka

import org.apache.kafka.common.KafkaException as ApacheKafkaCommonKafkaException

abstract class KafkaException(message: String?) : ApacheKafkaCommonKafkaException(message)
