FROM openjdk:11-jdk
VOLUME /tmp
ARG JAR_FILE=build/libs/farmus-farm-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} farmus-farm.jar

ENTRYPOINT ["java","-jar","/farmus-farm.jar"]