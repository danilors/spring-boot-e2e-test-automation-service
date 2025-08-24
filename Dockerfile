FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app
COPY . .
CMD ["mvn", "clean", "verify"]

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
COPY --from=build /app/target/surefire-reports /app/report-out