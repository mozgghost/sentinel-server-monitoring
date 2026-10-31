FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /workspace

COPY pom.xml ./pom.xml

COPY backend/pom.xml ./backend/pom.xml
COPY backend/src ./backend/src

COPY monitoring-agent/pom.xml ./monitoring-agent/pom.xml
COPY monitoring-agent/src ./monitoring-agent/src

RUN mvn -f pom.xml -pl backend -am -DskipTests package

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /workspace/backend/target/sentinel-backend-0.1.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]