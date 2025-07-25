FROM eclipse-temurin:21-jdk
WORKDIR /app
ARG JAR_FILE=target/billing-service-*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 9092
ENTRYPOINT ["java", "-jar", "app.jar"]