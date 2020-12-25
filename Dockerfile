FROM adoptopenjdk/openjdk11:ubi
ADD target/docker-proxy.jar /app.jar
ENTRYPOINT ["java","-jar", "-Dspring.profiles.active=docker","/app.jar"]