# 1단계: Gradle로 .jar 빌드 (JDK 필요)
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# 의존성 레이어 캐시 (소스 변경 시 재다운로드 방지)
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts ./
RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon

# 소스 복사 후 빌드
COPY src src
RUN ./gradlew bootJar --no-daemon

# 2단계: JRE만으로 실행 (컴파일러 불필요 → 이미지 경량화)
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Xmx256m", "-jar", "app.jar"]
