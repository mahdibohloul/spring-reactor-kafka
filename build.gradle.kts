plugins {
  kotlin("jvm") version "1.9.25"
  kotlin("plugin.spring") version "1.9.25"
  id("io.spring.dependency-management") version "1.1.7"

  id("com.vanniktech.maven.publish") version "0.34.0"
  id("com.diffplug.spotless") version "7.2.1"
  id("io.gitlab.arturbosch.detekt") version "1.23.6"

  `java-library`
}

group = "io.github.mahdibohloul"
version = "0.9.0"
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
  implementation("org.springframework.kafka:spring-kafka:3.3.10")
  implementation("org.springframework:spring-messaging:6.2.10")
  implementation("box.tapsi.libs:utilities-starter:0.9.3")
  implementation("io.github.mahdibohloul:kpring-mediatr-starter:2.0.1")
  implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.4")
  implementation("io.projectreactor.addons:reactor-extra:3.5.3")
  implementation("org.slf4j:slf4j-api:2.0.17")
  implementation("com.google.protobuf:protobuf-java:3.25.5")
  implementation("com.fasterxml.jackson.core:jackson-databind:2.20.0")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.20.0")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.8.1")

  testImplementation("org.springframework.boot:spring-boot-starter-test:3.5.5")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:1.9.23")
  testImplementation("io.projectreactor:reactor-test:3.7.9")
  testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")

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


mavenPublishing {
  publishToMavenCentral()
  signAllPublications()

  pom {
    name.set("spring-reactor-kafka")
    description.set("Spring Reactor Kafka integration library")
    url.set("https://github.com/mahdibohloul/spring-reactor-kafka")
    licenses {
      license {
        name.set("MIT License")
        url.set("https://opensource.org/licenses/MIT")
        distribution.set("repo")
      }
    }
    developers {
      developer {
        id.set("mahdibohloul")
        name.set("Mahdi Bohloul")
        email.set("mahdiibohloul@gmail.com")
        url.set("https://github.com/mahdibohloul/")
      }
    }
    scm {
      url.set("https://github.com/mahdibohloul/spring-reactor-kafka")
    }
  }
}

spotless {
  kotlin {
    target("src/**/*.kt", "spring-reactor-kafka-samples/src/**/*.kt")
    ktlint()
      .editorConfigOverride(
        mapOf(
          "indent_size" to 2,
          "ktlint_standard_filename" to "disabled",
          "ktlint_standard_max-line-length" to "120"
        )
      )
    trimTrailingWhitespace()
    leadingTabsToSpaces()
    endWithNewline()
  }
}

detekt {
  buildUponDefaultConfig = true
  allRules = true
  config.setFrom("$projectDir/detekt.yml")
  baseline = file("$projectDir/detekt-baseline.xml")
}

tasks.register("verifyReadmeContent") {
  doLast {
    val readmeFile = file("README.md")
    val content = readmeFile.readText()

    // List of checks
    val checks = listOf(
      Check("group ID", """<groupId>${project.group}</groupId>"""),
      Check("version", """<version>${project.version}</version>"""),
    )

    val errors = checks.mapNotNull { check ->
      if (!content.contains(check.expectedValue)) {
        "Missing or incorrect ${check.name}: ${check.expectedValue}"
      } else null
    }

    if (errors.isNotEmpty()) {
      throw GradleException(
        """
                README content verification failed!
                ${errors.joinToString("\n")}
                Please update the README.md with correct values
            """.trimIndent()
      )
    }
  }
}

tasks.check {
  dependsOn("verifyReadmeContent")
}

data class Check(val name: String, val expectedValue: String)
