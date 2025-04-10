FROM openjdk:17-jdk-alpine

ARG JAR_FILE=target/*.jar

#ARG STRIPE_API_KEY=${STRIPE_API_KEY}
#ARG SPRING_MAIL_USERNAME=${SPRING_MAIL_USERNAME}
#ARG SPRING_MAIL_PASSWORD=${SPRING_MAIL_PASSWORD}
#ARG JWT_SECRET_KEY=${JWT_SECRET_KEY}

#ENV STRIPE_API_KEY=${STRIPE_API_KEY}
#ENV SPRING_MAIL_USERNAME=${SPRING_MAIL_USERNAME}
#ENV SPRING_MAIL_PASSWORD=${SPRING_MAIL_PASSWORD}
#ENV JWT_SECRET_KEY=${JWT_SECRET_KEY}

RUN mkdir -p /app/images

WORKDIR /app

COPY ${JAR_FILE} /app/biteandsip-0.0.1.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/biteandsip-0.0.1.jar"]
