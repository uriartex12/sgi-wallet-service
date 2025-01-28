FROM openjdk:17
COPY target/wallet-service-0.0.1-SNAPSHOT.jar wallet-service.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/wallet-service.jar"]
