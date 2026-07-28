# Nutzen eines leichten Java 21 Runtimes
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Kopiert das bereits vom Gradle-Build erstellte JAR
COPY build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]