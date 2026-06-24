plugins {
    java
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "com.busanbank"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Web
    implementation("org.springframework.boot:spring-boot-starter-web")

    // JPA + MySQL
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("com.mysql:mysql-connector-j")

    // Validation
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Cache (Caffeine)
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine")

    // AOP (거래 로깅)
    implementation("org.springframework.boot:spring-boot-starter-aop")

    // Security (BCrypt 패스워드 인코더만 사용)
    implementation("org.springframework.boot:spring-boot-starter-security")

    // Mail (이메일 인증)
    implementation("org.springframework.boot:spring-boot-starter-mail")

    // API 문서
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")

    // JSON 직렬화 (로그 마스킹용)
    implementation("com.fasterxml.jackson.core:jackson-databind")

    // Anthropic Claude SDK (AI 상품 요약)
    implementation("com.anthropic:anthropic-java:2.34.0")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.h2database:h2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
