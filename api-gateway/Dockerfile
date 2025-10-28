FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /workspace

# Copy maven wrapper
COPY api-gateway/mvnw .
COPY api-gateway/.mvn .mvn

# Copy custom Maven settings
COPY maven-settings.xml /root/.m2/settings.xml

# Copy api-gateway pom first (better layer caching)
COPY api-gateway/pom.xml api-gateway/

# Copy source code
COPY api-gateway/src api-gateway/src

# Build api-gateway (downloads dependencies automatically)
WORKDIR /workspace/api-gateway
RUN chmod +x ../mvnw && \
    ../mvnw clean package -DskipTests -B || \
    ../mvnw clean package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy jar from build stage
COPY --from=build /workspace/api-gateway/target/*.jar app.jar

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

ENTRYPOINT ["java", "-jar", "app.jar"]
