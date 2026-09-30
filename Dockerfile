# ---------- Stage 1: Build the jar using Maven + JDK 17 ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml first so Docker can cache downloaded dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Now copy the actual source code and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# ---------- Stage 2: Run the jar with a lightweight JRE ----------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/target/ecommerce-backend-1.0.0.jar app.jar

# Render sets $PORT automatically; application.yml already reads it via ${PORT:8080}
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
