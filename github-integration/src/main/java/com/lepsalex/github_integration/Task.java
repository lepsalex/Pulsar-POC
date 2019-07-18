package com.lepsalex.github_integration;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Task {
    private final long id;
    private final String name;
    private final String description;
}