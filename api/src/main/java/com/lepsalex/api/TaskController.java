package com.lepsalex.api;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.val;

@RestController
public class TaskController {

    @GetMapping("/task/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable(value = "id") Long taskId) {
        val dummyTask = new Task(taskId, "Not a Real Task", "Only being used to demo this api");
        return ResponseEntity.ok().body(dummyTask);
    }

    @PostMapping("/task")
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) {
        // Add message publish here
        return ResponseEntity.ok().body(task);
    }

    @PutMapping("/task/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable(value = "id") Long taskId, @Valid @RequestBody Task task) {
        // Add message publish here
        return ResponseEntity.ok().body(task);
    }
}