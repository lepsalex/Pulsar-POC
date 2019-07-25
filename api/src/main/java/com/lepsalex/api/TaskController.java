package com.lepsalex.api;

import javax.validation.Valid;

import com.lepsalex.integrations.TaskProtos;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
public class TaskController {

    @Autowired
    TaskProducer taskProducer;

    @PostMapping("/task")
    public ResponseEntity<TaskProtos.Task> createTask(@Valid @RequestBody TaskProtos.Task task) {
        // Publish event to queue
        try {
            taskProducer.taskNewProducer.send(task);
        } catch (PulsarClientException ex) {
            log.error(ex);
        }
        
        return ResponseEntity.ok().body(task);
    }

    @PutMapping("/task/{id}")
    public ResponseEntity<TaskProtos.Task> updateTask(@PathVariable(value = "id") Long taskId, @Valid @RequestBody TaskProtos.Task task) {
        // Publish event to queue
        try {
            taskProducer.taskUpdateProducer.send(task);
        } catch (PulsarClientException ex) {
            log.error(ex);
        }
        
        // Add message publish here
        return ResponseEntity.ok().body(task);
    }
}