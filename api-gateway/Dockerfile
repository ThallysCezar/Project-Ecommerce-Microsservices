FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /workspace

# Copy maven wrapper
COPY mvnw .
COPY .mvn .mvn

# Copy pom first (better layer caching)
COPY pom.xml .

# Copy source code
COPY src src

# Build (downloads dependencies automatically)
RUN chmod +x mvnw && \
    ./mvnw clean package -DskipTests -B || \
    ./mvnw clean package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy jar from build stage
COPY --from=build /workspace/target/*.jar app.jar

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

ENTRYPOINT ["java", "-jar", "app.jar"]
