FROM openjdk:17-jdk-alpine

ARG JAR_FILE=target/*.jar

RUN mkdir -p /app/images

WORKDIR /app

COPY ${JAR_FILE} /app/biteandsip-app.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/biteandsip-app.jar"]
