import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType
import org.owasp.dependencycheck.gradle.extension.AnalyzerExtension

plugins {
    id("org.springframework.boot") version "2.6.4"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"

    kotlin("jvm") version "1.6.10"
    kotlin("plugin.spring") version "1.6.10"
    kotlin("plugin.jpa") version "1.6.10"
    kotlin("plugin.allopen") version "1.6.10"

    id("org.jlleitschuh.gradle.ktlint") version "10.2.1"
    id("org.jlleitschuh.gradle.ktlint-idea") version "10.2.1"

    id("io.gitlab.arturbosch.detekt").version("1.19.0")

    jacoco
    id("org.jetbrains.kotlinx.kover") version "0.5.0"

    id("org.owasp.dependencycheck") version "7.0.1"

    id("com.github.ben-manes.versions") version "0.42.0"

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
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.flywaydb:flyway-core:8.5.4")
    runtimeOnly("com.h2database:h2:2.1.210")

    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("org.springdoc:springdoc-openapi-kotlin:1.6.6")
    implementation("org.springdoc:springdoc-openapi-ui:1.6.6")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(module = "mockito-core")
    }

    testImplementation("io.mockk:mockk:1.12.3")
    testImplementation("com.ninja-squad:springmockk:3.1.1")
}

springBoot {
    mainClass.set("mirogaudi.memo.MemoServiceApplicationKt")
}

tasks.jar {
    enabled = false
}

allOpen {
    annotations(
        "javax.persistence.Entity",
        "javax.persistence.MappedSuperclass",
        "javax.persistence.Embedabble"
    )
}

ktlint {
    version.set("0.45.1")

    ignoreFailures.set(false)

    reporters {
        reporter(ReporterType.HTML)
        reporter(ReporterType.JSON)
    }
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
        html.required.set(true)
        csv.required.set(true)
    }
}

kover {
    // Use for IntelliJ based coverage
    coverageEngine.set(kotlinx.kover.api.CoverageEngine.INTELLIJ)

    // Use for JaCoCo based coverage
    // coverageEngine.set(kotlinx.kover.api.CoverageEngine.JACOCO)
    // jacocoEngineVersion.set("0.8.7")

    generateReportOnCheck = false
}
tasks.koverHtmlReport {
    isEnabled = true
}
tasks.koverMergedHtmlReport {
    isEnabled = true
}
tasks.koverXmlReport {
    isEnabled = false
}
tasks.koverMergedXmlReport {
    isEnabled = false
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
