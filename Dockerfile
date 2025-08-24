FROM maven:3.9.6-eclipse-temurin-21 AS build



FROM eclipse-temurin:21-jre
WORKDIR /app
COPY . .
# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN ["mvn", "clean", "verify"]

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
COPY --from=build /app/target/surefire-reports /app/report-out
COPY --from=build /app/target/surefire-reports /app/report-out

