# Build stage - use a modern, widely available Maven image
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /build

# copy pom & wrapper first to cache dependency layer
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN mvn -B dependency:go-offline

# copy sources and build (skip tests to speed image build; run tests in CI)
COPY src ./src
RUN mvn -B clean package -DskipTests

# Runtime stage - small JRE image
FROM eclipse-temurin:17-jre
WORKDIR /app

# copy the jar produced by Maven to a stable name
COPY --from=build /build/target/*.jar /app/app.jar

# create non-root user (optional)
RUN addgroup --system app && adduser --system --ingroup app app || true
USER app

EXPOSE 8080

# Prefer SPRING_PROFILES_ACTIVE env var (set in compose)
ENTRYPOINT ["sh", "-c", "java -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-docker} -Dspring.datasource.url=${SPRING_DATASOURCE_URL:-jdbc:mysql://mysql:3306/supplychainx} -Xms256m -Xmx512m -jar /app/app.jar"]