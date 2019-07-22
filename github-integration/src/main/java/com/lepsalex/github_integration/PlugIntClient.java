package com.lepsalex.github_integration;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.ConsumerBuilder;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class PlugIntClient {
    PulsarClient client;

    public List<String> topics = Arrays.asList(
            "task-new",
            "task-update"
    );

    public void run() {

        log.info("Github Integration Service Start!");

        try {
            val url = System.getenv("SERVICE_URL") != null ? System.getenv("SERVICE_URL") : "pulsar://localhost:6650";

            client = PulsarClient
                    .builder()
                    .allowTlsInsecureConnection(true)
                    .serviceUrl(url)
                    .build();

            ConsumerBuilder consumerBuilder = client
                    .newConsumer()
                    .subscriptionName("github-integration-service");

            Consumer<Task> consumer = consumerBuilder
                    .topics(topics)
                    .subscribe();

            log.info("Github Integration Service Running ...");

            receiveMessageFromConsumer(consumer);
        } catch (PulsarClientException err) {
            log.error(err.getMessage());
        }
    }

    public void shutdown() {
        try {
            log.warn("Github Service Integration Shutting Down!");
            client.close();
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
