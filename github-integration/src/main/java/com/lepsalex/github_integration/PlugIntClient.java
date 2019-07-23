package com.lepsalex.github_integration;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.pulsar.client.api.*;
import org.apache.pulsar.client.impl.schema.JSONSchema;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.util.*;

@Slf4j
public class PlugIntClient {
    PulsarClient pulsarClient;
    GithubClient gitHubClient;

    public enum topic {
        TASK_NEW,
        TASK_UPDATE
    }

    public Map<topic, String> topics = new HashMap<topic, String>() {
        {
            put(topic.TASK_NEW, "task-new");
            put(topic.TASK_UPDATE, "task-update");
        }
    };

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

            ConsumerBuilder consumerBuilder = pulsarClient
                    .newConsumer(JSONSchema.of(Task.class))
                    .subscriptionName("github-integration-service");

            Consumer<Task> consumer = consumerBuilder
                    .topics(Arrays.asList(topics.values()))
                    .subscribe();

            receiveMessageFromConsumer(consumer);
        } catch (PulsarClientException err) {
            log.error(err.getMessage());
            closePulsarConnection();
            return;
        }

        log.info("Github Integration Service Running ...");
    }

    public void closePulsarConnection() {
        try {
            log.warn("Github Service Integration Shutting Down!");
            pulsarClient.close();
        } catch (PulsarClientException err) {
            log.error("Failed to close Github Service Integration properly (plugIntClient connection not closed): " + err.getMessage());
        }
    }

    private void receiveMessageFromConsumer(Consumer<Task> consumer) {
        log.info(String.format("Listening on topics: %s", String.join(", ", topics.values())));

        consumer.receiveAsync().thenAccept(message -> {
            Optional<GHIssue> issue;

            try {
                val topicType = topic.valueOf(message.getTopicName().toUpperCase());

                switch (topicType) {
                    case TASK_NEW:
                        issue = gitHubClient.createIssue(message.getValue());
                        break;
                    case TASK_UPDATE:
                        issue = gitHubClient.updateIssue(message.getValue());
                        break;
                    default:
                        log.warn(String.format("Unknown topic type: %s", message.getTopicName()));
                        issue = Optional.empty();
                }

                if (issue.isPresent()) {
                    consumer.acknowledge(message.getMessageId());
                } else {
                    consumer.negativeAcknowledge(message.getMessageId());
                    log.warn("Failed to create issue for: ", message.getData());
                }
            } catch (PulsarClientException err) {
                consumer.negativeAcknowledge(message.getMessageId());
                log.error(err.getMessage());
            }

            receiveMessageFromConsumer(consumer);
        });
    }
}
