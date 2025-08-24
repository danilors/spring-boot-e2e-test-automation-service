WORKDIR /app
COPY . .
CMD ["mvn", "clean", "test", "verify"]

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
COPY --from=build /app/target/surefire-reports /app/report-out