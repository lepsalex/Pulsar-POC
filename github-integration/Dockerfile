FROM java:8-jdk-alpine

COPY ./target/github-integration-1.0-SNAPSHOT.jar /usr/app/

WORKDIR /usr/app

RUN sh -c 'touch github-integration-1.0-SNAPSHOT.jar'

ENTRYPOINT ["java","-jar","github-integration-1.0-SNAPSHOT.jar"]
