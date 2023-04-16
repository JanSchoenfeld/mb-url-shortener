FROM openjdk:17
ADD target/mb-url-shortener.jar mb-url-shortener.jar
ENTRYPOINT ["java", "-jar","mb-url-shortener.jar"]
EXPOSE 8080