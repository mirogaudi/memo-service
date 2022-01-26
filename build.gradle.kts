import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType
import org.owasp.dependencycheck.gradle.extension.AnalyzerExtension

plugins {
    id("org.springframework.boot") version "2.6.3"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"

    kotlin("jvm") version "1.6.10"
    kotlin("plugin.spring") version "1.6.10"
    kotlin("plugin.jpa") version "1.6.10"

    id("org.jlleitschuh.gradle.ktlint") version "10.2.1"
    id("org.jlleitschuh.gradle.ktlint-idea") version "10.2.1"

    jacoco
    id("org.jetbrains.kotlinx.kover") version "0.5.0-RC2"

    id("org.owasp.dependencycheck") version "6.5.3"
    id("com.github.ben-manes.versions") version "0.41.0"

    id("org.barfuin.gradle.taskinfo") version "1.3.1"

    id("com.palantir.docker") version "0.32.0"
}

group = "mirogaudi"
version = "1.0.0"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    // implementation("org.flywaydb:flyway-core:8.4.2")
    runtimeOnly("com.h2database:h2:2.1.210")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("org.springdoc:springdoc-openapi-ui:1.6.5")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(module = "mockito-core")
    }

    testImplementation("io.mockk:mockk:1.12.2")
    testImplementation("com.ninja-squad:springmockk:3.1.0")
}

springBoot {
    mainClass.set("mirogaudi.memo.MemoServiceApplicationKt")
}
tasks.jar {
    enabled = false
}

ktlint {
    version.set("0.43.2")

    ignoreFailures.set(false)

    reporters {
        reporter(ReporterType.HTML)
        reporter(ReporterType.JSON)
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

tasks.withType<Test> {
    useJUnitPlatform {
        excludeEngines("junit-vintage")
    }

    testLogging {
        events = setOf(TestLogEvent.FAILED, TestLogEvent.SKIPPED, TestLogEvent.PASSED)
        showExceptions = true
        showStackTraces = false
    }
}

jacoco {
    toolVersion = "0.8.7"
}
tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        csv.required.set(true)
    }
}

kover {
    coverageEngine.set(kotlinx.kover.api.CoverageEngine.INTELLIJ)

    // coverageEngine.set(kotlinx.kover.api.CoverageEngine.JACOCO)
    // jacocoEngineVersion.set("0.8.7")

    generateReportOnCheck = false
}
tasks.koverHtmlReport {
    isEnabled = true
}
tasks.koverXmlReport {
    isEnabled = true
}

dependencyCheck {
    analyzers(
        closureOf<AnalyzerExtension> {
            assemblyEnabled = false
        }
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
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

docker {
    val imageName = "${project.group}/${project.name}"
    name = "$imageName:$version"
    tag("Latest", "$imageName:latest")

    val bootJarTask = tasks.bootJar.get()
    files(bootJarTask.archiveFile)
    buildArgs(mapOf("JAR_FILE" to bootJarTask.archiveFileName.get()))
}
