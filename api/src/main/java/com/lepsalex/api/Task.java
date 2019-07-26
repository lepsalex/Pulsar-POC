package com.lepsalex.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class Task {
    private final long id;
    @NonNull private final String project;
    @NonNull private final String title;
    @NonNull private final String description;
    @NonNull private final String status;
}
