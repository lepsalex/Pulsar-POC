package com.lepsalex.github_integration;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.ConsumerBuilder;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class PlugIntClient {
    PulsarClient pulsarClient;
    GitHub gitHub;
    GHRepository repo;

    public List<String> topics = Arrays.asList(
            "task-new",
            "task-update"
    );

    public void run() {

        log.info("Github Integration Service Start!");

        log.info("Connecting to Github ...");

        try {
            gitHub = GitHub.connect();
            repo = gitHub.getRepository("lepsalex/Pulsar-POC");
        } catch (IOException err) {
            log.error("Error getting github project!", err);
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
                    .newConsumer()
                    .subscriptionName("github-integration-service");

            Consumer<Task> consumer = consumerBuilder
                    .topics(topics)
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
        log.info(String.format("Listening on topics: %s", String.join(", ", topics)));

        consumer.receiveAsync().thenAccept(message -> {
            try {
                consumer.acknowledge(message.getMessageId());
                log.info(new String(message.getData()));
            } catch (PulsarClientException err) {
                consumer.negativeAcknowledge(message.getMessageId());
                log.error(err.getMessage());
            }

            receiveMessageFromConsumer(consumer);
        });
    }
}
