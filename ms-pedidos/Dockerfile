FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app

# Copy parent pom and common-dtos first (for better layer caching)
COPY ../common-dtos/pom.xml ../common-dtos/pom.xml
COPY ../common-dtos/src ../common-dtos/src

# Copy maven wrapper and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

ENTRYPOINT ["java", "-jar", "app.jar"]
