package io.github.mahdibohloul.spring.reactor.kafka.consumer.discovery

import io.github.mahdibohloul.spring.reactor.kafka.consumer.annotaitons.KafkaController
import io.github.mahdibohloul.spring.reactor.kafka.consumer.annotaitons.ReactiveKafkaListener
import io.github.mahdibohloul.spring.reactor.kafka.consumer.services.KafkaConsumerService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.ApplicationContext
import org.springframework.core.annotation.AnnotationUtils

class KafkaConsumerDiscovery(
  private val applicationContext: ApplicationContext,
  private val kafkaConsumerService: KafkaConsumerService,
) : InitializingBean {
  private val logger = LoggerFactory.getLogger(this::class.java)

  override fun afterPropertiesSet() {
    try {
      logger.info("Starting Kafka consumers auto-configuration")

      val kafkaControllers = applicationContext.getBeansWithAnnotation(KafkaController::class.java)
      logger.info("Found ${kafkaControllers.size} Kafka controllers to configure")

      kafkaControllers.forEach(::registerController)

      logger.info("Completed Kafka consumers auto-configuration successfully")
    } catch (e: Exception) {
      logger.error("Failed to auto-configure Kafka consumers", e)
      throw IllegalStateException("Kafka consumer auto-configuration failed", e)
    }
  }

  private fun registerController(beanName: String, controller: Any) {
    logger.debug("Processing Kafka controller: $beanName")

    val kafkaListenerMethods = controller.javaClass.methods
      .filter { method ->
        AnnotationUtils.findAnnotation(method, ReactiveKafkaListener::class.java) != null
      }

    if (kafkaListenerMethods.isEmpty()) {
      logger.warn("No @ReactiveKafkaListener methods found in controller: $beanName")
      return
    }

    kafkaListenerMethods.forEach { method ->
      try {
        logger.debug("Registering Kafka listener method: $beanName.${method.name}")
        kafkaConsumerService.registerKafkaListener(method, controller)
        logger.debug("Successfully registered Kafka listener method: $beanName.${method.name}")
      } catch (e: Exception) {
        logger.error("Failed to register Kafka listener method: $beanName.${method.name}", e)
        throw IllegalStateException("Failed to register Kafka listener", e)
      }
    }

    logger.info(
      "Successfully registered ${kafkaListenerMethods.size} listener(s) for controller: $beanName",
    )
  }
}
