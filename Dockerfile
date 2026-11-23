# Backend de la clinica (Spring Boot, Java 21)
# Etapa 1: compilar el .jar con Maven
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
RUN mvn -q -DskipTests package

# Etapa 2: imagen liviana solo con el JRE y el .jar
FROM eclipse-temurin:21-jre
WORKDIR /app
ENV TZ=America/Bogota
COPY --from=build /app/target/clinica-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
