# syntax=docker/dockerfile:1
# ===== build =====
FROM gradle:8.8-jdk21 AS build
WORKDIR /app
COPY build.gradle.kts settings.gradle.kts ./
COPY src ./src
# Gradle 의존성 캐시를 BuildKit 캐시마운트로 보존 → 재빌드 시 재다운로드 안 함
RUN --mount=type=cache,target=/home/gradle/.gradle \
    gradle bootJar --no-daemon -x test

# ===== run =====
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
