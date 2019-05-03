# Pulsar-POC
A quick proof-of-concept to demonstrate apache pulsar capabilities and the general advantages of a microservice arch.

## Services

### API
Spring Boot REST service that simulates task creation/updating/deleting - published on appropriate topics

### Github Service
Simple python script that connects to Pulsar and outputs messages to the screen from the topics that the api publishes to