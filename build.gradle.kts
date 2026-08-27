import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import dev.detekt.gradle.Detekt
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    val kotlinVersion = "2.4.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    kotlin("plugin.allopen") version kotlinVersion

    id("org.springframework.boot") version "4.1.1"
    id("io.spring.dependency-management") version "1.1.7"

    id("org.jmailen.kotlinter") version "5.7.0"
    id("dev.detekt") version "2.0.0-alpha.6"

    jacoco
    id("org.jetbrains.kotlinx.kover") version "0.9.9"

    id("org.owasp.dependencycheck") version "13.0.0"
    id("io.github.ben-manes.versions") version "0.61.0"

    id("com.bmuschko.docker-remote-api") version "10.0.0"

    id("org.barfuin.gradle.taskinfo") version "3.0.2"
}

group = "mirogaudi"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("tools.jackson.module:jackson-module-kotlin")

    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    implementation("org.springframework.boot:spring-boot-h2console")
    runtimeOnly("com.h2database:h2")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.1.0")

    // Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation("io.mockk:mockk:1.14.11")
    testImplementation("com.ninja-squad:springmockk:5.0.1")
}

kotlin {
    jvmToolchain(25)

    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

// needed when using data classes for entities
allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.test {
    useJUnitPlatform()

    testLogging {
        events = setOf(TestLogEvent.FAILED, TestLogEvent.SKIPPED, TestLogEvent.PASSED)
        showExceptions = true
        showStackTraces = false
    }

    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).takeIf { it > 0 } ?: 1
}

kotlinter {
    ktlintVersion = "1.8.0"
    ignoreFormatFailures = false
    ignoreLintFailures = false
    reporters = arrayOf("checkstyle")
}

detekt {
    buildUponDefaultConfig = true
    ignoreFailures = false
}
tasks.withType<Detekt>().configureEach {
    // FIXME enable as soon as kotlin v2.4.10 supported
    enabled = false

    jvmTarget = "1.8"

    reports {
        html.required.set(true)
        markdown.required.set(true)
        checkstyle.required.set(false)
        sarif.required.set(false)
    }
}

jacoco {
    toolVersion = "0.8.15"
}
tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        xml.required.set(true)
        csv.required.set(true) // used by jacoco-badge-generator
        html.required.set(true)
    }
}

tasks.named<DependencyUpdatesTask>("dependencyUpdates") {
    revision = "release"
    outputFormatter = "html,json"
    rejectVersionIf {
        candidate.version.isNonStable() && !currentVersion.isNonStable()
    }
}
fun String.isNonStable(): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(this)
    return isStable.not()
}

tasks.register<DockerBuildImage>("dockerBuildImage") {
    group = LifecycleBasePlugin.BUILD_GROUP
    description = "Builds project Docker image."

    dependsOn(tasks.bootJar)

    inputDir.set(file("${project.projectDir}"))
    buildArgs.set(mapOf("APP_VERSION" to "$version"))
    val imageName = "${project.group}/${project.name}"
    images.set(setOf("$imageName:$version", "$imageName:latest"))
    remove.set(true)
}
