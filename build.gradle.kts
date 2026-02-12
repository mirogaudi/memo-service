@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "2.3.0"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    kotlin("plugin.allopen") version kotlinVersion

    id("org.springframework.boot") version "3.5.9"
    id("io.spring.dependency-management") version "1.1.7"

    id("org.jmailen.kotlinter") version "5.3.0"
    id("io.gitlab.arturbosch.detekt") version "1.23.8"

    jacoco
    id("org.jetbrains.kotlinx.kover") version "0.9.7"

    id("org.owasp.dependencycheck") version "12.2.0"
    id("com.github.ben-manes.versions") version "0.53.0"

    id("org.springdoc.openapi-gradle-plugin") version "1.9.0"
    id("com.bmuschko.docker-remote-api") version "10.0.0"
    id("org.barfuin.gradle.taskinfo") version "3.0.1"
}

group = "mirogaudi"
version = "1.0.0"
description = "Demo memos (notes) service"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.flywaydb:flyway-core:11.20.1")
    runtimeOnly("com.h2database:h2:2.4.240")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.15")

    // Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.mockito", module = "mockito-core")
        exclude(group = "org.mockito", module = "mockito-junit-jupiter")
    }

    testImplementation(kotlin("test"))

    testImplementation(platform("org.junit:junit-bom:6.0.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation("io.mockk:mockk:1.14.9")
    testImplementation("com.ninja-squad:springmockk:5.0.1")
}

springBoot {
    mainClass.set("mirogaudi.memo.MemoServiceApplicationKt")
}

tasks.jar {
    enabled = false
}

kotlin {
    kotlinDaemonJvmArgs = listOf("-Xmx1024m", "-Xms256m", "-XX:+UseParallelGC")
}

// needed when using data classes for entities
allOpen {
    annotations(
        "jakarta.persistence.Entity",
        "jakarta.persistence.Embeddable",
        "jakarta.persistence.MappedSuperclass"
    )
}

kotlinter {
    ignoreLintFailures = false
    reporters = arrayOf("html", "json")
}

detekt {
    buildUponDefaultConfig = true
    ignoreFailures = true
}
tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true)

        xml.required.set(false)
        txt.required.set(false)
        sarif.required.set(false)
    }
}
project.afterEvaluate {
    configurations["detekt"].resolutionStrategy.eachDependency {
        if (requested.group == "org.jetbrains.kotlin") {
            useVersion("2.0.21")
        }
    }
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
    }
}

tasks.withType<Test>().configureEach {
    // enables JUnit
    useJUnitPlatform()

    testLogging {
        events = setOf(TestLogEvent.FAILED, TestLogEvent.SKIPPED, TestLogEvent.PASSED)
        showExceptions = true
        showStackTraces = false
    }

    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).takeIf { it > 0 } ?: 1
}

jacoco {
    toolVersion = "0.8.14"
}
tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(true) // used by jacoco-badge-generator
    }
}

kover {
    // Uncomment to use JaCoCo
    // useJacoco()
}

dependencyCheck {
    analyzers {
        assemblyEnabled = false
    }
}

tasks.dependencyUpdates {
    revision = "release"
    rejectVersionIf {
        isNonStable(candidate.version)
    }

    outputFormatter = "html,json"
}
fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

openApi {
    apiDocsUrl.set("http://localhost:8080/ms/v3/api-docs")
    outputDir.set(layout.buildDirectory.dir("docs"))
    outputFileName.set("memo-service-openapi.json")
}

tasks.register<DockerBuildImage>("dockerBuildImage") {
    description = "Builds project Docker image."
    group = LifecycleBasePlugin.BUILD_GROUP

    dependsOn(tasks.bootJar)

    inputDir.set(file("${project.projectDir}"))

    val imageName = "${project.group}/${project.name}"
    images.set(setOf("$imageName:$version", "$imageName:latest"))
}
