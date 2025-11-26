FROM rootpublic/maven:3-openjdk-17-bookworm-slim AS build
WORKDIR /app
COPY . .
RUN mvn clean package assembly:single -DskipTests

FROM eclipse-temurin:17-jdk
COPY --from=build /app/target/*-jar-with-dependencies.jar app.jar
CMD ["java", "-jar", "/app.jar"]
