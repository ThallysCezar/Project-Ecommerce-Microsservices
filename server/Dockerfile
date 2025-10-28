FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /workspace

# Copy maven wrapper
COPY server/mvnw .
COPY server/.mvn .mvn

# Copy custom Maven settings
COPY maven-settings.xml /root/.m2/settings.xml

# Copy server pom first (better layer caching)
COPY server/pom.xml server/

# Copy source code
COPY server/src server/src

# Build server (downloads dependencies automatically)
WORKDIR /workspace/server
RUN chmod +x ../mvnw && \
    ../mvnw clean package -DskipTests -B || \
    ../mvnw clean package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy jar from build stage
COPY --from=build /workspace/server/target/*.jar app.jar

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

ENTRYPOINT ["java", "-jar", "app.jar"]
