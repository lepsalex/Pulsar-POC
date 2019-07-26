package com.lepsalex.github_integration;

import java.util.Optional;

public interface IntegrationClient<T> {
    Optional<T> createIssue(Task task);
    Optional<T> updateIssue(Task task);
}
