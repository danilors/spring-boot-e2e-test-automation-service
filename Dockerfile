FROM maven:3.9.7-eclipse-temurin-21 AS build

WORKDIR /app

COPY . .

RUN mvn test

# The test reports will be available in /app/target