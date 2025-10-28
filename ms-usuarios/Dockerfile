FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /workspace

# Copy maven wrapper
COPY ms-usuarios/mvnw .
COPY ms-usuarios/.mvn .mvn

# Copy custom Maven settings (mirror brasileiro)
COPY maven-settings.xml /root/.m2/settings.xml

# Copy common-dtos (dependency)
COPY common-dtos common-dtos/

# Install common-dtos in local maven repo
WORKDIR /workspace/common-dtos
RUN chmod +x ../mvnw && ../mvnw clean install -DskipTests

# Copy ms-usuarios pom first (better layer caching)
WORKDIR /workspace
COPY ms-usuarios/pom.xml ms-usuarios/

# Copy source code
COPY ms-usuarios/src ms-usuarios/src

# Build ms-usuarios (downloads dependencies automatically)
WORKDIR /workspace/ms-usuarios
RUN chmod +x ../mvnw && \
    ../mvnw clean package -DskipTests -B || \
    ../mvnw clean package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy jar from build stage
COPY --from=build /workspace/ms-usuarios/target/*.jar app.jar

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

ENTRYPOINT ["java", "-jar", "app.jar"]
