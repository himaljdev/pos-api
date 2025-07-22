FROM eclipse-temurin:21-jdk
WORKDIR /app
ARG JAR_FILE=target/cloud-service-*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "app.jar"]