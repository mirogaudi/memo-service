import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.internal.KaptWithoutKotlincTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.owasp.dependencycheck.gradle.extension.AnalyzerExtension
import java.util.Locale

plugins {
    id("org.springframework.boot") version "3.1.0"
    id("io.spring.dependency-management") version "1.1.0"

    val kotlinVersion = "1.8.21"
    kotlin("jvm") version kotlinVersion
    kotlin("kapt") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    kotlin("plugin.allopen") version kotlinVersion

    id("org.jmailen.kotlinter") version "3.15.0"
    id("io.gitlab.arturbosch.detekt").version("1.23.0")

    jacoco
    id("org.jetbrains.kotlinx.kover") version "0.7.0"

    id("org.owasp.dependencycheck") version "8.2.1"
    id("com.github.ben-manes.versions") version "0.46.0"

    id("org.springdoc.openapi-gradle-plugin") version "1.6.0"
    id("com.bmuschko.docker-remote-api") version "9.3.1"
    id("org.barfuin.gradle.taskinfo") version "2.1.0"
}

group = "mirogaudi"
version = "1.0.0"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    kapt("org.springframework.boot:spring-boot-configuration-processor")
    // uncomment to enable hibernate metamodel generator
    // kapt("org.hibernate:hibernate-jpamodelgen")

    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.flywaydb:flyway-core:9.19.1")
    runtimeOnly("com.h2database:h2")

    val springdocVersion = "2.1.0"
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springdocVersion")
    implementation("org.springdoc:springdoc-openapi-starter-common:$springdocVersion")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(group = "org.mockito", module = "mockito-core")
    }

    testImplementation(kotlin("test"))

    testImplementation("io.mockk:mockk:1.13.5")
    testImplementation("com.ninja-squad:springmockk:4.0.2")
}

springBoot {
    mainClass.set("mirogaudi.memo.MemoServiceApplicationKt")
}

tasks.jar {
    enabled = false
}

kotlin {
    kotlinDaemonJvmArgs = listOf("-Xmx486m", "-Xms256m", "-XX:+UseParallelGC")
}

tasks.withType<KaptWithoutKotlincTask>().configureEach {
    kaptProcessJvmArgs.add("-Xmx256m")
}

// needed when using data classes for entities
allOpen {
    annotations(
        "jakarta.persistence.Entity",
        "jakarta.persistence.Embeddable",
        "jakarta.persistence.MappedSuperclass",
    )
}

kotlinter {
    ignoreFailures = false
    reporters = arrayOf("html", "json")
}

detekt {
    buildUponDefaultConfig = true
    ignoreFailures = true
}
tasks.withType<Detekt>().configureEach {
    jvmTarget = JavaVersion.VERSION_17.toString()

    reports {
        html.required.set(true)

        xml.required.set(false)
        txt.required.set(false)
        sarif.required.set(false)
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

tasks.withType<Test>().configureEach {
    // enables JUnit5
    useJUnitPlatform {
        excludeEngines("junit-vintage")
    }

    testLogging {
        events = setOf(TestLogEvent.FAILED, TestLogEvent.SKIPPED, TestLogEvent.PASSED)
        showExceptions = true
        showStackTraces = false
    }

    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).takeIf { it > 0 } ?: 1
}

jacoco {
    toolVersion = "0.8.10"
}
tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(true) // used by jacoco-badge-generator
    }
}

// Uncomment to use JaCoCo in Kover
/*kover {
    useJacoco()
}*/

dependencyCheck {
    analyzers(
        closureOf<AnalyzerExtension> {
            assemblyEnabled = false
        },
    )
}

tasks.dependencyUpdates {
    revision = "release"
    rejectVersionIf {
        isNonStable(candidate.version)
    }

    outputFormatter = "html,json"
}
fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase(Locale.getDefault()).contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

openApi {
    apiDocsUrl.set("http://localhost:8080/ms/v3/api-docs")
    outputDir.set(file("$buildDir/docs"))
    outputFileName.set("memo-service-openapi.json")
}

tasks.register<DockerBuildImage>("dockerBuildImage") {
    dependsOn(tasks.bootJar)

    inputDir.set(file("${project.projectDir}"))

    val imageName = "${project.group}/${project.name}"
    images.set(setOf("$imageName:$version", "$imageName:latest"))
}
