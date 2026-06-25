# ===== build =====
FROM gradle:8.8-jdk21 AS build
WORKDIR /app
COPY . .
# bootJar 만 실행하면 실행 가능한 단일 jar 1개만 생성됨(plain jar 미생성). 테스트는 DB 필요 → 제외
RUN gradle bootJar --no-daemon -x test

# ===== run =====
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
