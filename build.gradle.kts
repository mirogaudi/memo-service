import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType
import org.owasp.dependencycheck.gradle.extension.AnalyzerExtension

plugins {
    id("org.springframework.boot") version "2.7.0"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"

    val kotlinVersion = "1.6.21"
    kotlin("jvm") version kotlinVersion
    kotlin("kapt") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    kotlin("plugin.allopen") version kotlinVersion

    val ktlintVersion = "10.3.0"
    id("org.jlleitschuh.gradle.ktlint") version ktlintVersion
    id("org.jlleitschuh.gradle.ktlint-idea") version ktlintVersion

    id("io.gitlab.arturbosch.detekt").version("1.20.0")

    jacoco
    id("org.jetbrains.kotlinx.kover") version "0.5.1"

    id("org.owasp.dependencycheck") version "7.1.0.1"
    id("com.github.ben-manes.versions") version "0.42.0"

    id("org.barfuin.gradle.taskinfo") version "1.4.0"

    id("org.springdoc.openapi-gradle-plugin") version "1.3.4"

    id("com.palantir.docker") version "0.33.0"
}

group = "mirogaudi"
version = "1.0.0"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    kapt("org.springframework.boot:spring-boot-configuration-processor")
    // hibernate metamodel generator
    kapt("org.hibernate:hibernate-jpamodelgen")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.flywaydb:flyway-core:8.5.11")
    runtimeOnly("com.h2database:h2:2.1.212")

    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    val springdocVersion = "1.6.8"
    implementation("org.springdoc:springdoc-openapi-ui:$springdocVersion")
    implementation("org.springdoc:springdoc-openapi-kotlin:$springdocVersion")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(module = "mockito-core")
    }

    testImplementation("io.mockk:mockk:1.12.4")
    testImplementation("com.ninja-squad:springmockk:3.1.1")
}

springBoot {
    mainClass.set("mirogaudi.memo.MemoServiceApplicationKt")
}

tasks.jar {
    enabled = false
}

// needed when using data classes for entities
allOpen {
    annotations(
        "javax.persistence.Entity",
        "javax.persistence.Embedabble",
        "javax.persistence.MappedSuperclass"
    )
}

ktlint {
    version.set("0.45.2")

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
    // use for IntelliJ based coverage
    coverageEngine.set(kotlinx.kover.api.CoverageEngine.INTELLIJ)

    // use for JaCoCo based coverage
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

openApi {
    apiDocsUrl.set("http://localhost:8080/ms/v3/api-docs")
    outputDir.set(file("$buildDir/docs"))
    outputFileName.set("memo-service-openapi.json")
}

docker {
    val imageName = "${project.group}/${project.name}"
    name = "$imageName:$version"
    tag("Latest", "$imageName:latest")

    val bootJarTask = tasks.bootJar.get()
    files(bootJarTask.archiveFile)
    buildArgs(mapOf("JAR_FILE" to bootJarTask.archiveFileName.get()))
}
