FROM openjdk

WORKDIR /app

COPY target/cliente-*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
