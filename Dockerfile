FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

ARG PROFILE=dev
ENV SPRING_PROFILES_ACTIVE=${PROFILE}

COPY --from=build /app/target/*.jar app.jar
EXPOSE 10041
ENTRYPOINT ["java","-jar","/app/app.jar"]
