# Build stage
FROM gradle:8.4-jdk17 AS build
COPY --chown=gradle:gradle src /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

# Package stage
FROM eclipse-temurin:17-jre
COPY --from=build /home/gradle/src/build/libs/BudgetPlanner.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
