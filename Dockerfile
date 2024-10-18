FROM openjdk:17-jdk-slim
EXPOSE 8081
ADD target/gestion-station-ski-1.0.jar docker-spring-boot.jar
ENTRYPOINT ["java","-jar","/docker-spring-boot.jar"]
