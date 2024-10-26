FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY target/user-service-0.0.1-SNAPSHOT.jar /app/user-service.jar

EXPOSE 8082

RUN apk add --no-cache curl

ENTRYPOINT ["java", "-jar", "user-service.jar"]