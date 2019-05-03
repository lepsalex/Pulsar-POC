package com.lepsalex.api;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Task {
    private final long id;
    private final String name;
    private final String description;
}