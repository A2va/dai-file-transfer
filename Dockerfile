# Use maven base image for building
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app
COPY . .
RUN mvn spotless:apply clean package

# Use JRE for runtime
FROM eclipse-temurin:21

WORKDIR /app
COPY --from=builder /app/target/file-transfer-1.0-SNAPSHOT.jar file-transfer.jar

EXPOSE 2000
ENTRYPOINT ["java", "-jar", "file-transfer.jar"]