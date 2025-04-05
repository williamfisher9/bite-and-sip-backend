FROM openjdk:17-jdk-alpine

ARG JAR_FILE=target/*.jar

RUN mkdir -p /app

WORKDIR /app

COPY ${JAR_FILE} /app/biteandsip-0.0.1-SNAPSHOT.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/biteandsip-0.0.1-SNAPSHOT.jar"]
