FROM maven:3.9.7-eclipse-temurin-21 AS build

WORKDIR /app

COPY . .

RUN mvn clean verify && \
    mkdir -p /app/report-out && \
    cp -r /app/target/surefire-reports /app/report-out/

CMD ["tail", "-f", "/dev/null"]