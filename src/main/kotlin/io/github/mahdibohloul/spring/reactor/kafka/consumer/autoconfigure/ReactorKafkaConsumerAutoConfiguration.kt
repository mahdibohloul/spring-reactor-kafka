package io.github.mahdibohloul.spring.reactor.kafka.consumer.autoconfigure

import io.github.mahdibohloul.spring.reactor.kafka.autoconfigure.ReactiveKafkaAutoConfiguration
import io.github.mahdibohloul.spring.reactor.kafka.consumer.ConsumerProperties
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan

@EnableConfigurationProperties(ConsumerProperties::class)
@AutoConfiguration(after = [ReactiveKafkaAutoConfiguration::class])
@ComponentScan(basePackages = ["io.github.mahdibohloul.spring.reactor.kafka.consumer"])
class ReactorKafkaConsumerAutoConfiguration
