import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.spring") version "2.2.21"
    kotlin("plugin.jpa") version "2.2.21"
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.sonarqube") version "7.0.0.6105"
    jacoco
}

group = "no.nav"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_21



configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

val logstashLogbackEncoderVersion by extra("7.4")
val tokenSupportVersion by extra("4.1.4")
val springCloudVersion by extra("2022.0.0-RC2")
val retryVersion by extra("2.0.5")
val postgresqlVersion by extra("42.7.2")
val awailitilityKotlinVersion by extra("4.2.1")
val assertkJvmVersion by extra("0.28.0")
val springMockkVersion by extra("4.0.2")
val mockkVersion by extra("1.13.10")
val okHttp3Version by extra("4.12.0")
val orgJsonVersion by extra("20240303")
val springdocVersion by extra("2.5.0")
val testcontainersVersion by extra("1.19.7")

ext["testcontainersVersion"] = testcontainersVersion

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    implementation("org.yaml:snakeyaml:2.5") {
        because("https://github.com/navikt/k9-brukerdialog-cache/security/dependabot/1")
    }


    implementation("no.nav.security:token-validation-spring:$tokenSupportVersion")
    testImplementation("no.nav.security:token-validation-spring-test:$tokenSupportVersion")

    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework:spring-aspects")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "mockito-core")
    }

    // Swagger (openapi 3)
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springdocVersion")

    // Metrics
    implementation("io.micrometer:micrometer-registry-prometheus")

    // Logging
    implementation("net.logstash.logback:logstash-logback-encoder:$logstashLogbackEncoderVersion")

    // Database
    runtimeOnly("org.postgresql:postgresql:$postgresqlVersion")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    testImplementation("org.testcontainers:junit-jupiter:$testcontainersVersion")
    testImplementation("org.testcontainers:postgresql:$testcontainersVersion")

    //Kafka
    implementation("org.springframework.kafka:spring-kafka")
    constraints {
        implementation("org.scala-lang:scala-library") {
            because("org.apache.kafka:kafka_2.13:3.3.2 -> https://www.cve.org/CVERecord?id=CVE-2022-36944")
            version {
                require("2.13.9")
            }
        }
    }
    testImplementation("org.springframework.kafka:spring-kafka-test")

    // Jackson
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    // Diverse
    implementation("org.json:json:$orgJsonVersion")
    implementation("com.github.ben-manes.caffeine:caffeine")
    testImplementation("org.awaitility:awaitility-kotlin:$awailitilityKotlinVersion")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:$assertkJvmVersion")
    testImplementation("com.ninja-squad:springmockk:$springMockkVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
}

dependencyManagement {
    imports {
        mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}")
    }
}

tasks {
    withType<Test> {
        useJUnitPlatform()
        testLogging {
            exceptionFormat = TestExceptionFormat.FULL
            showStackTraces = true
        }
        finalizedBy(jacocoTestReport) // report is always generated after tests run
    }

    jacocoTestReport {
        dependsOn(test) // tests are required to run before generating the report
        reports {
            xml.required.set(true)
            csv.required.set(false)
        }
    }

    withType<KotlinCompile> {
        compilerOptions {
            freeCompilerArgs.set(listOf("-Xjsr305=strict"))
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    getByName<Jar>("jar") {
        enabled = false
    }

    withType<Wrapper> {
        gradleVersion = "8.5"
    }
}

sonarqube {
    properties {
        property("sonar.projectKey", "navikt_k9-brukerdialog-cache")
        property("sonar.organization", "navikt")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.login", System.getenv("SONAR_TOKEN"))
        property("sonar.sourceEncoding", "UTF-8")
    }
}
