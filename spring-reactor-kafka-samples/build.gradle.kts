plugins {
  kotlin("jvm") version "1.9.25"
  kotlin("plugin.spring") version "1.9.25"
  id("org.springframework.boot") version "3.5.5"
  id("io.spring.dependency-management") version "1.1.7"
  application
}

group = "io.github.mahdibohloul"
version = "0.0.1-SNAPSHOT"
description = "spring-reactor-kafka-samples"

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(21)
  }
}

repositories {
  mavenCentral()
}

dependencies {
  // Main library dependency
  implementation(project(":"))

  // Spring Boot dependencies
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.kafka:spring-kafka:3.3.10")

  // JSON processing
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

  // Logging
  implementation("org.springframework.boot:spring-boot-starter-logging")

  // Reactor and Kafka
  implementation("io.projectreactor:reactor-core")
  implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
  implementation("io.projectreactor.addons:reactor-extra")
  implementation("io.projectreactor.kafka:reactor-kafka:1.3.24")

  // Testing
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
  testImplementation("io.projectreactor:reactor-test")

  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
  compilerOptions {
    freeCompilerArgs.addAll("-Xjsr305=strict")
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}

// Configure the application plugin
application {
  mainClass.set("io.github.mahdibohloul.spring.reactor.kafka.samples.SpringReactorKafkaSamplesApplicationKt")
}
