package com.lepsalex.github_integration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
class Task {
    private final long id;
    @NonNull final String project;
    @NonNull private final String title;
    @NonNull private final String description;
    @NonNull private final String status;
}