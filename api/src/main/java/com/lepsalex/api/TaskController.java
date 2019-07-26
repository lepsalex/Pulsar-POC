package com.lepsalex.api;

import com.lepsalex.integrations.TaskProtos;
import lombok.extern.log4j.Log4j2;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Log4j2
public class TaskController {

    @Autowired
    TaskProducer taskProducer;

    @PostMapping("/task")
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) {
        // Publish event to queue
        try {
            taskProducer.taskNewProducer.send(task);
        } catch (PulsarClientException ex) {
            log.error(ex);
        }
        
        return ResponseEntity.ok().body(task);
    }

    @PutMapping("/task/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable(value = "id") Long taskId, @Valid @RequestBody Task task) {
        // Publish event to queue
        try {
            taskProducer.taskUpdateProducer.send(task);
        } catch (PulsarClientException ex) {
            log.error(ex);
        }
        
        // Add message publish here
        return ResponseEntity.ok().body(task);
    }

    private TaskProtos.Task convertTaskToProto(Task task) {
        return TaskProtos.Task.newBuilder()
                .setId(task.getId())
                .setProject(task.getProject())
                .setTitle(task.getTitle())
                .setDescription(task.getDescription())
                .setStatus(task.getStatus())
                .build();
    }
}