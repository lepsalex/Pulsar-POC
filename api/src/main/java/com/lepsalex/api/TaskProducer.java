package com.lepsalex.api;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.NoArgsConstructor;
import lombok.val;
import lombok.extern.log4j.Log4j2;

import org.apache.pulsar.client.api.CompressionType;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.impl.schema.JSONSchema;

@Log4j2
@Component
@NoArgsConstructor
public class TaskProducer {

    @Value("${SERVICE_URL:pulsar://localhost:6650}")
    private String SERVICE_URL;

    private String CREATE_TOPIC_NAME = "task-new";
    private String UPDATE_TOPIC_NAME = "task-update";

    public PulsarClient client;
    public Producer<Task> taskNewProducer;
    public Producer<Task> taskUpdateProducer;

    @PostConstruct
    public void startClient() throws IOException {
        try {
            client = PulsarClient.builder()
                .allowTlsInsecureConnection(true)
                .serviceUrl(SERVICE_URL)
                .build();

            val msgSchema = JSONSchema.of(Task.class);

            taskNewProducer = client.newProducer(msgSchema).topic(CREATE_TOPIC_NAME)
                    .compressionType(CompressionType.LZ4).create();
    
            log.info("Created producer for the topic {}", CREATE_TOPIC_NAME);
    
            taskUpdateProducer = client.newProducer(msgSchema).topic(UPDATE_TOPIC_NAME)
                    .compressionType(CompressionType.LZ4).create();
    
            log.info("Created producer for the topic {}", UPDATE_TOPIC_NAME);
        } catch (PulsarClientException ex) {
            log.error(ex.getMessage());
        }   
    }
}
