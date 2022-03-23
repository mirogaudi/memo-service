![Lines of code](https://img.shields.io/tokei/lines/github/mirogaudi/memo-service)
![GitHub top language](https://img.shields.io/github/languages/top/mirogaudi/memo-service)
![GitHub gradle workflow status](https://img.shields.io/github/workflow/status/mirogaudi/memo-service/Java_CI_with_Gradle)
![JaCoCo coverage](https://img.shields.io/endpoint?url=https://raw.githubusercontent.com/mirogaudi/memo-service/main/.github/badges/jacoco.json)
![JaCoCo branches](https://img.shields.io/endpoint?url=https://raw.githubusercontent.com/mirogaudi/memo-service/main/.github/badges/branches.json)
![GitHub license](https://img.shields.io/github/license/mirogaudi/memo-service)

# MEMO Service with REST API

## Description

Application is a demo of a simple memos (notes) service

### Used technologies

- Kotlin (jvmTarget 17)
- Gradle (wrapper)
- Spring Boot
- Spring Web MVC
- Spring Data JPA
- Flyway DB migration tool
- H2 in-memory DB
- Docker
- OpenAPI 3 & Swagger UI (springdoc-openapi)
- JUnit Jupiter
- MockK
- Springmockk
- Ktlint
- Detekt
- JaCoCo
- Kover (with IntelliJ coverage engine)

#### Misc

- Repository is licensed under [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.html)
- Repository uses GitHub Actions ([Java_CI_with_Gradle](.github/workflows/gradle.yml))
- Repository uses Dependabot version updates ([dependabot.yml](.github/dependabot.yml))
- Readme badges are rendered using [shields.io](https://github.com/badges/shields)
- JaCoCo badges are generated by [jacoco-badge-generator](https://github.com/cicirello/jacoco-badge-generator)
- ASCII Art for [SpringBoot banner](src/main/resources/banner.txt) was generated
  with [TAAG](http://patorjk.com/software/taag/) (font Calvin S)

### Functionality

- Application implements CRUD operations for memos (notes)
- Memo service is `@Transactional` to avoid race condition

### Database

- Application DB stores simple text memos with creation and due dates
- DB is initialized with Flyway
    - [V1__create_schema.sql](src/main/resources/db/migration/V1__create_schema.sql) (initially generated with Spring
      Data JPA, for details see [application.yml](src/main/resources/application.yml))
    - [V2__insert_data.sql](src/main/resources/db/migration/V2__insert_data.sql)
- Application uses H2 in-memory DB

### Configuration

See configuration in [application.yml](src/main/resources/application.yml)

## Getting started

### Gradle build

```shell
# Build with Gradle wrapper
$ ./gradlew clean build

# Build with Gradle wrapper continuing on task failures
$ ./gradlew --continue clean build
``` 

### Docker build

```shell
# Build and tag docker image with Docker (requires artifacts to be already built)
$ docker build -t mirogaudi/memo-service:1.0.0 .
$ docker tag mirogaudi/memo-service:1.0.0 mirogaudi/memo-service:latest

# Build docker image with Gradle wrapper
$ ./gradlew clean docker dockerTagLatest
```

### Run

```shell
# Run with Java
$ java -jar build/libs/memo-service-1.0.0.jar

# Run with Gradle wrapper
$ ./gradlew bootRun
  
# Run with Docker
$ docker run -it -d --rm --name memo-service -p 8080:8080 mirogaudi/memo-service:latest
```

- Or just run `MemoServiceApplication` in an IDE

### View and try API

- OpenAPI docs: [http://localhost:8080/ms/v3/api-docs](http://localhost:8080/ms/v3/api-docs)
- Swagger UI: [http://localhost:8080/ms/swagger-ui/index.html](http://localhost:8080/ms/swagger-ui/index.html)

### View DB

- H2 console [http://localhost:8080/ms/h2-console](http://localhost:8080/ms/h2-console)
    - url: `jdbc:h2:mem:ms`
    - username: `sa`
    - password: `<empty>`

## Code quality

### Linter

```shell
# Lint with ktlint using Gradle wrapper
$ ./gradlew --continue clean ktlintCheck
```

#### Code style

```shell
# Apply ktlint Kotlin code style to IntelliJ IDEA project scheme 
$ ./gradlew ktlintApplyToIdea
```

### Static code analysis

```shell
# Check code with detekt using Gradle wrapper
$ ./gradlew clean detekt

```

### Code coverage

```shell
# Run tests with Gradle wrapper generating JaCoCo code coverage report 
$ ./gradlew clean jacocoTestReport

# Run tests with Gradle wrapper generating Kover code coverage report (with IntelliJ coverage engine)
$ ./gradlew clean koverReport

```

### Dependencies vulnerabilities

```shell
# Generate OWASP dependency vulnerability report with Gradle wrapper
$ ./gradlew dependencyCheckAnalyze
```

## Maintenance

### Update dependencies

```shell
# Check for dependency updates with Gradle wrapper
$ ./gradlew dependencyUpdates

# Update Gradle wrapper
$ ./gradlew wrapper --gradle-version <version>
```

### Check Gradle task dependencies

```shell
# Show Gradle build task dependencies tree
$ ./gradlew tiTree build

# Show Gradle build task dependencies order 
$ ./gradlew tiOrder build
```

## TODO:

- setup springdoc + swagger + asciidoctor
- add Task to Memo
- add db schema diagramm (plantUML)
- add V2__insert_data.sql
- implement classes
    - Memo repo
    - Memo service
    - Memo controller
    - tests
- use Micrometer
- use Spring WebFlux
