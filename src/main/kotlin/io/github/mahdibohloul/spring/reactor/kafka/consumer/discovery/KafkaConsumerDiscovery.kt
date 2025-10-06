package io.github.mahdibohloul.spring.reactor.kafka.consumer.discovery

import io.github.mahdibohloul.spring.reactor.kafka.consumer.annotaitons.KafkaController
import io.github.mahdibohloul.spring.reactor.kafka.consumer.annotaitons.ReactiveKafkaListener
import io.github.mahdibohloul.spring.reactor.kafka.consumer.services.KafkaConsumerService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.ApplicationContext
import org.springframework.core.annotation.AnnotationUtils

/**
 * Handles the discovery and registration of Kafka controllers and listener methods annotated with
 * [ReactiveKafkaListener]. This class is responsible for scanning the Spring application context for all beans
 * annotated with [KafkaController], identifying listener methods within those beans, and registering them via the
 * [KafkaConsumerService].
 *
 * This class ensures that:
 * 1. All methods annotated with [ReactiveKafkaListener] in Kafka controllers are discovered.
 * 2. Each listener method is validated and registered with the help of `KafkaConsumerService`.
 * 3. Any errors during the discovery or registration process
 * are logged and propagated as part of the initialization phase.
 *
 * Implements [InitializingBean] to automatically trigger Kafka consumer auto-configuration during application startup.
 *
 * @constructor Creates a `KafkaConsumerDiscovery` instance, requiring:
 * - `applicationContext`: The Spring application context used to retrieve beans annotated with [KafkaController].
 * - `kafkaConsumerService`: The service responsible for registering discovered Kafka listener methods.
 */
@Suppress("detekt.TooGenericExceptionCaught")
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
