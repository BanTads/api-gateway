FROM openjdk

WORKDIR /app

COPY target/auth-*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
