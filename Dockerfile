FROM openjdk:17-jdk-alpine
ARG JAR_FILE=target/hello-world-0.0.1-SNAPSHOT.jar
ARG JKS_FILE=keystore.jks
COPY ${JAR_FILE} app.jar
COPY ${JKS_FILE} keystore.jks
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app.jar", "--server.port=8081", "--server.address=0.0.0.0", "--server.ssl.enabled=true", "--server.ssl.key-store=keystore.jks", "--server.ssl.keyStoreType=JKS", "--server.ssl.key-store-password=123456"]
