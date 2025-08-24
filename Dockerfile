

FROM eclipse-temurin:21-jre

# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN ["mvn", "clean", "verify"]


