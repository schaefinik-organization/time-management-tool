# Nutzen eines leichten Java 21 Runtimes
FROM eclipse-temurin:21-jre-alpine

# Setze das Arbeitsverzeichnis im Container
WORKDIR /app

# Wir kopieren gezielt das fertige ausführbare JAR.
# Falls Spring Boot ein "-plain.jar" erzeugt, schließen wir es so elegant aus.
# Bei den meisten Spring Boot Projekten endet die Datei auf .jar, aber nicht auf plain.jar.
COPY build/libs/*-SNAPSHOT.jar app.jar

# Informelle Dokumentation, auf welchem Port der Container intern lauscht
EXPOSE 8080

# Startbefehl für die Spring Boot App
ENTRYPOINT ["java", "-jar", "app.jar"]