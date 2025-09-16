package io.github.mahdibohloul.spring.reactor.kafka

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringReactorKafkaApplication

fun main(args: Array<String>) {
  runApplication<SpringReactorKafkaApplication>(*args)
}
