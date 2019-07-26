package com.lepsalex.github_integration;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.kohsuke.github.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class GithubClient {
    GitHub gitHub;

    final private Map<String, String> projectToRepoMapping = new HashMap<String, String>() {
        {
            put("awesome", "lepsalex/Pulsar-POC");
            put("possum", "lepsalex/Pulsar-POC");
            put("murder", "lepsalex/Pulsar-POC");
        }
    };

    public GithubClient() throws IOException {
        log.info("Connecting to Github ...");
        gitHub = GitHub.connect();
    }

    public Optional<GHIssue> createIssue(Task task){
        try {
            val repo = getRepo(task.getProject());
            return createIssue(task, repo);
        } catch (IOException err) {
            log.error("Error creating issue!", err);
            return Optional.empty();
        }
    }

    private Optional<GHIssue> createIssue(Task task, GHRepository repo) {
        try {
            val newIssue = Optional.of(repo.createIssue(task.getTitle())
                    .body(task.getDescription())
                    .label(task.getProject())
                    .create());

            log.info("Issue succesfully udpated!", newIssue);
            return newIssue;
        } catch (IOException err) {
            log.error("Error getting github project!", err);
            return Optional.empty();
        }
    }

    public Optional<GHIssue> updateIssue(Task task) {
        try {
            val repo = getRepo(task.getProject());
            Optional<GHIssue> issue = findIssue(repo, task.getTitle(), task.getProject());

            if (issue.isPresent()) {
                val existingIssue = issue.get();
                try {
                    existingIssue.setBody(task.getDescription());

                    if (task.getStatus().equals("open")) {
                        existingIssue.reopen();
                    } else {
                        existingIssue.close();
                    }

                    log.info("Issue successfully updated!");
                    return Optional.of(existingIssue);
                } catch (IOException err) {
                    log.error("Error updating issue!", err);
                    return Optional.empty();
                }
            } else {
                return createIssue(task, repo);
            }
        } catch (IOException err) {
            log.error("Error updating issue!", err);
            return Optional.empty();
        }
    }

    private Optional<GHIssue> findIssue(GHRepository repo, String title, String project) throws IOException {
        return repo.getIssues(GHIssueState.ALL)
                .stream()
                .filter(issue -> title.equals(issue.getTitle()) && findLabel(issue, project).isPresent())
                .findFirst();
    }

    private Optional<GHLabel> findLabel(GHIssue issue, String labelName) {
        try {
            return issue.getLabels()
                    .stream()
                    .filter(label -> label.getName().equals(labelName))
                    .findFirst();
        } catch (IOException err) {
            log.error(String.format("Error searching for label %s!", labelName), err);
            return Optional.empty();
        }
    }

    private GHRepository getRepo(String project) throws IOException {
        return gitHub.getRepository(projectToRepoMapping.get(project));
    }
}
