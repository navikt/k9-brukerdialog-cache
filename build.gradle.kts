import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.10"
    kotlin("plugin.spring") version "1.9.10"
    kotlin("plugin.jpa") version "1.9.10"
    id("org.springframework.boot") version "3.1.3"
    id("io.spring.dependency-management") version "1.1.3"
    id("org.sonarqube") version "4.3.0.3225"
    jacoco
}

group = "no.nav"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17



configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

val logstashLogbackEncoderVersion by extra("7.2")
val tokenSupportVersion by extra("3.0.8")
val springCloudVersion by extra("2022.0.0-RC2")
val retryVersion by extra("2.0.2")
val postgresqlVersion by extra("42.5.1")
val awailitilityKotlinVersion by extra("4.2.0")
val assertkJvmVersion by extra("0.25")
val springMockkVersion by extra("3.1.2")
val mockkVersion by extra("1.13.2")
val guavaVersion by extra("31.1-jre")
val okHttp3Version by extra("4.10.0")
val orgJsonVersion by extra("20230227")
val springdocVersion by extra("2.0.0")
val testcontainersVersion by extra("1.17.6")

ext["testcontainersVersion"] = testcontainersVersion

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    implementation("org.yaml:snakeyaml:2.2") {
        because("https://github.com/navikt/k9-brukerdialog-cache/security/dependabot/1")
    }


    implementation("no.nav.security:token-validation-spring:$tokenSupportVersion")
    testImplementation("no.nav.security:token-validation-spring-test:$tokenSupportVersion")

    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web") {
        //exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
    }
    //implementation("org.springframework.boot:spring-boot-starter-jetty")
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
    implementation("com.google.guava:guava:$guavaVersion")
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
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "17"
        }
    }

    getByName<Jar>("jar") {
        enabled = false
    }

    withType<Wrapper> {
        gradleVersion = "8.2.1"
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
