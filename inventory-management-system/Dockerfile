# Build stage
FROM maven:3.8-openjdk-21 as builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY . .
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:21-slim-bullseye
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75.0"
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
