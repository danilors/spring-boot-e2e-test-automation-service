FROM maven:3.9.7-eclipse-temurin-21 AS build

WORKDIR /app

COPY . .

CMD ["mvn", "clean", "surefire-report:report"]