FROM openjdk:17-jdk-slim
RUN apt-get update && apt-get install -y maven
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests
RUN mkdir -p target
RUN mv target/*.jar target/auth.jar
CMD ["java", "-jar", "target/auth.jar"]
