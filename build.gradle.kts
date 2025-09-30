plugins {
  kotlin("jvm") version "1.9.25"
  kotlin("plugin.spring") version "1.9.25"
  id("io.spring.dependency-management") version "1.1.7"
  `java-library`
}

group = "io.github.mahdibohloul"
version = "0.0.1-SNAPSHOT"
description = "spring-reactor-kafka"

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(21)
  }
}

configurations {
  compileOnly {
    extendsFrom(configurations.annotationProcessor.get())
  }
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.springframework:spring-context:6.2.10")
  implementation("org.springframework.boot:spring-boot-autoconfigure:3.5.5")
  implementation("io.projectreactor:reactor-core:3.7.9")
  implementation("io.projectreactor.kafka:reactor-kafka:1.3.24")
  implementation("box.tapsi.libs:utilities-starter:0.9.3")
  implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.4")
  implementation("io.projectreactor.addons:reactor-extra:3.5.3")
  implementation("org.slf4j:slf4j-api:2.0.17")
  implementation("com.google.protobuf:protobuf-java:3.25.5")

  testImplementation("org.springframework.boot:spring-boot-starter-test:3.5.5")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:1.9.23")
  testImplementation("io.projectreactor:reactor-test:3.7.9")

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
