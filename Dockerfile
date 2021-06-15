FROM maven:3.5.2-jdk-8

COPY pom.xml /pom.xml
COPY data /data
COPY src /src

RUN mvn package

CMD ["java", "-jar", "target/api-desafio-0.0.1-SNAPSHOT.jar"]