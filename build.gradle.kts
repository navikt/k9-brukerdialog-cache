import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.6.7"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
    kotlin("plugin.jpa") version "1.6.21"
}

group = "no.nav"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

val logstashLogbackEncoderVersion by extra("7.0.1")
val tokenSupportVersion by extra("2.0.14")
val springCloudVersion by extra("2021.0.1")
val retryVersion by extra("1.3.0")
val zalandoVersion by extra("0.27.0")
val hibernateTypes52Version by extra("2.15.1")
val awailitilityKotlinVersion by extra("4.1.1")
val assertkJvmVersion by extra("0.25")
val springMockkVersion by extra("3.1.1")
val mockkVersion by extra("1.12.3")
val guavaVersion by extra("31.1-jre")
val okHttp3Version by extra("4.9.3")
val orgJsonVersion by extra("20220320")
val springdocVersion by extra("1.6.7")
val testcontainersVersion by extra("1.17.1")

ext["okhttp3.version"] = okHttp3Version
ext["testcontainersVersion"] = testcontainersVersion

repositories {
    mavenCentral()
}

dependencies {

    implementation("no.nav.security:token-validation-spring:$tokenSupportVersion")
    testImplementation("no.nav.security:token-validation-spring-test:$tokenSupportVersion")
    testImplementation("com.squareup.okhttp3:okhttp:$okHttp3Version")

    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
    }
    implementation("org.springframework.boot:spring-boot-starter-jetty")
    implementation("org.springframework:spring-aspects")
    runtimeOnly("org.springframework.boot:spring-boot-properties-migrator")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.springframework.boot:spring-boot-autoconfigure-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "junit")
        exclude(module = "mockito-core")
    }
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")

    // Swagger (openapi 3)
    implementation("org.springdoc:springdoc-openapi-kotlin:$springdocVersion")
    implementation("org.springdoc:springdoc-openapi-ui:$springdocVersion")

    // Metrics
    implementation("io.micrometer:micrometer-registry-prometheus")

    // Logging
    implementation("net.logstash.logback:logstash-logback-encoder:$logstashLogbackEncoderVersion")

    // Database
    runtimeOnly("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-core")
    runtimeOnly("org.hibernate:hibernate-jpamodelgen")
    implementation("com.vladmihalcea:hibernate-types-52:$hibernateTypes52Version")
    testImplementation("org.testcontainers:junit-jupiter:$testcontainersVersion")
    testImplementation("org.testcontainers:postgresql:$testcontainersVersion")

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
    implementation("org.zalando:problem-spring-web-starter:$zalandoVersion")
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

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.getByName<Jar>("jar") {
    enabled = false
}
