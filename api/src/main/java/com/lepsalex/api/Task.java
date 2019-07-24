package com.lepsalex.api;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Task {
    private final long id;
    private final String project;
    private final String title;
    private final String description;
    private final String status;
}