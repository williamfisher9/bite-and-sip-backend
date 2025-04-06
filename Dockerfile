FROM openjdk:17-jdk-alpine

ARG JAR_FILE=target/*.jar
ARG STRIPE_KEY=${{ secrets.STRIPE_API_KEY }}
ARG MAIL_USERNAME=${{ secrets.SPRING_MAIL_USERNAME }}
ARG MAIL_PASSWORD=${{ secrets.SPRING_MAIL_PASSWORD }}

ENV STRIPE_API_KEY=${STRIPE_KEY}
ENV SPRING_MAIL_USERNAME=${MAIL_USERNAME}
ENV SPRING_MAIL_PASSWORD=${MAIL_PASSWORD}

RUN mkdir -p /app/images

WORKDIR /app

COPY ${JAR_FILE} /app/biteandsip-app.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/biteandsip-app.jar"]
