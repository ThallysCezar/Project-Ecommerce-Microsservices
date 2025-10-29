FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /workspace

# Copy maven wrapper from ms-database
COPY ms-database/mvnw .
COPY ms-database/.mvn .mvn

# Copy common-dtos (dependency shared module)
COPY common-dtos common-dtos/

# Install common-dtos in local maven repo
WORKDIR /workspace/common-dtos
RUN chmod +x ../mvnw && \
    ../mvnw clean install -DskipTests -B || \
    ../mvnw clean install -DskipTests -B

# Copy ms-database pom first (better layer caching)
WORKDIR /workspace
COPY ms-database/pom.xml ms-database/

# Copy source code
COPY ms-database/src ms-database/src

# Build ms-database (downloads dependencies automatically)
WORKDIR /workspace/ms-database
RUN chmod +x ../mvnw && \
    ../mvnw clean package -DskipTests -B || \
    ../mvnw clean package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy jar from build stage
COPY --from=build /workspace/ms-database/target/*.jar app.jar

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

ENTRYPOINT ["java", "-jar", "app.jar"]
