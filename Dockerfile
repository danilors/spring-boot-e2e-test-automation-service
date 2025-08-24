FROM maven:3.9.6-eclipse-temurin-21 AS build



FROM eclipse-temurin:21-jre
WORKDIR /app
COPY . .
RUN ["mvn", "clean", "verify"]
COPY --from=build /app/target/surefire-reports /app/report-out

