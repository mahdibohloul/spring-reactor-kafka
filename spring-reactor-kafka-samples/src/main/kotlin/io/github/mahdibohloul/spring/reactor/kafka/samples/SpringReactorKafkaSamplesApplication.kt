package io.github.mahdibohloul.spring.reactor.kafka.samples

import io.github.mahdibohloul.spring.reactor.kafka.samples.producer.services.KafkaProducerService
import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class SpringReactorKafkaSamplesApplication {

  private val logger = LoggerFactory.getLogger(SpringReactorKafkaSamplesApplication::class.java)

  @Bean
  fun commandLineRunner(kafkaProducerService: KafkaProducerService): CommandLineRunner = CommandLineRunner {
    logger.info("🚀 Spring Reactor Kafka Samples Application started!")
    logger.info("📡 Kafka consumers are now listening for messages...")
    logger.info("🌐 REST API available at: http://localhost:8080/api/samples")
    logger.info("❤️  Health check available at: http://localhost:8080/api/samples/health")
    logger.info("📊 Actuator endpoints available at: http://localhost:8080/actuator")
    logger.info("")
    logger.info("📋 Available topics:")
    logger.info("   - user-events")
    logger.info("   - order-events")
    logger.info("   - notification-events")
    logger.info("")
    logger.info("💡 Use the REST API to send test messages to these topics!")
  }

  @PreDestroy
  fun shutdown() {
    logger.info("🛑 Shutting down Spring Reactor Kafka Samples Application...")
  }
}

fun main(args: Array<String>) {
  runApplication<SpringReactorKafkaSamplesApplication>(*args)
}
