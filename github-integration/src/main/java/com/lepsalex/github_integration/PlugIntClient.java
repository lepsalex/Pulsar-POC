package com.lepsalex.github_integration;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.pulsar.client.api.*;
import org.apache.pulsar.client.impl.schema.JSONSchema;
import org.kohsuke.github.GHIssue;

import java.io.IOException;
import java.util.*;

@Slf4j
public class PlugIntClient {
    PulsarClient pulsarClient;
    GithubClient gitHubClient;

    private String CREATE_TOPIC_NAME = "task-new";
    private String UPDATE_TOPIC_NAME = "task-update";

    public void run() {

        log.info("Github Integration Service Start!");

        log.info("Connecting to Github ...");

        try {
            gitHubClient = new GithubClient();
        } catch (IOException err) {
            log.error("Error connecting to GitHub!", err);
            return;
        }

        log.info("Github connected! ...");

        try {
            log.info("Connecting to PlugInt Server ...");

            val url = System.getenv("SERVICE_URL") != null ? System.getenv("SERVICE_URL") : "pulsar://localhost:6650";

            pulsarClient = PulsarClient
                    .builder()
                    .allowTlsInsecureConnection(true)
                    .serviceUrl(url)
                    .build();

            val topicList = Arrays.asList(CREATE_TOPIC_NAME, UPDATE_TOPIC_NAME);

            Consumer<Task> consumer = pulsarClient
                    .newConsumer(JSONSchema.of(Task.class))
                    .subscriptionName("github-integration-service")
                    .subscriptionType(SubscriptionType.Shared)
                    .topics(topicList)
                    .subscribe();

            log.info(String.format("Listening on topics: %s", String.join(", ", topicList)));

            do {
                Message<Task> message = consumer.receive();
                try {
                    handleMessage(consumer, message);
                } catch (SchemaSerializationException ex) {
                    consumer.negativeAcknowledge(message.getMessageId());
                    log.warn("Failed to handle message: ", message.getMessageId().toByteArray());
                }
            } while (true);
        } catch (PulsarClientException err) {
            log.error(err.getMessage());
            closePulsarConnection();
        }
    }

    public void closePulsarConnection() {
        try {
            log.warn("Github Service Integration Shutting Down!");
            pulsarClient.close();
        } catch (PulsarClientException err) {
            log.error("Failed to close Github Service Integration properly (plugIntClient connection not closed): " + err.getMessage());
        }
    }

    private void handleMessage(Consumer<Task> consumer, Message<Task> message) throws PulsarClientException, SchemaSerializationException {
        Optional<GHIssue> issue;

        if (message.getTopicName().contains(CREATE_TOPIC_NAME)) {
            issue = gitHubClient.createIssue(message.getValue());
        } else if (message.getTopicName().contains(UPDATE_TOPIC_NAME)) {
            issue = gitHubClient.updateIssue(message.getValue());
        } else {
            log.warn(String.format("Unknown topic type: %s", message.getTopicName()));
            issue = Optional.empty();
        }

        if (issue.isPresent()) {
            consumer.acknowledge(message);
            log.info("Message handled successfully!");
        } else {
            consumer.negativeAcknowledge(message.getMessageId());
            log.warn("Failed to handle message: ", message.getMessageId().toByteArray());
        }
    }
}
