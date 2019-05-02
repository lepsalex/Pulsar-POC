FROM java:8-jdk-alpine

COPY ./target/api-0.0.1-SNAPSHOT.jar /usr/app/

WORKDIR /usr/app

RUN sh -c 'touch api-0.0.1-SNAPSHOT.jar'

ENTRYPOINT ["java","-jar","api-0.0.1-SNAPSHOT.jar"]  