FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app
COPY . .

FROM eclipse-temurin:21-jre
RUN ["mvn", "clean", "verify"]
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
COPY --from=build /app/target/surefire-reports /app/report-out

