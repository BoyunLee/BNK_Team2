# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project state

This is a freshly scaffolded Spring Boot application with no business logic yet — only the main application class (`BusanbankLoanApplication`), a context-load smoke test, and a minimal `application.yaml`. Expect to build out the package structure (controllers, services, entities, repositories, security config) from scratch.

## Stack

- **Java 21** (Gradle toolchain — pins the JDK regardless of local `java` version)
- **Spring Boot 3.5.15** — Web (Spring MVC), Data JPA, Security
- **MySQL** via `mysql-connector-j` (runtime)
- **Lombok** (compile-time; requires annotation processing)

## Commands

Use the Gradle wrapper (`./gradlew`); do not rely on a system Gradle install.

```bash
./gradlew bootRun                          # Run the app locally
./gradlew build                            # Compile, run tests, assemble jar
./gradlew test                             # Run all tests
./gradlew test --tests "com.example.busanbank_loan.BusanbankLoanApplicationTests"   # Single test class
./gradlew test --tests "*.SomeClass.someMethod"                                     # Single test method
./gradlew bootJar                          # Build executable jar (build/libs/)
./gradlew clean                            # Remove build outputs
```

There is no separate lint task configured.

## Key conventions & gotchas

- **Package name:** the base package is `com.example.busanbank_loan` (underscore), *not* `busanbank-loan` — hyphens are invalid in Java package names. Keep new code under this package so component scanning from `@SpringBootApplication` picks it up.
- **Datasource is not yet configured.** `application.yaml` only sets the app name. The MySQL driver is on the classpath and `spring-boot-starter-data-jpa` auto-configures a `DataSource`, so the app (and the `@SpringBootTest` context-load test) will fail to start until `spring.datasource.*` properties are provided. When adding DB-dependent code, configure the datasource or provide a test profile/embedded DB.
- **Spring Security is on the classpath**, so all endpoints are locked behind HTTP Basic auth with a generated password by default until a `SecurityFilterChain` bean is defined.
- **Lombok** is wired through `annotationProcessor` — ensure annotation processing is enabled in the IDE for `@Getter`/`@Builder`/etc. to resolve.
