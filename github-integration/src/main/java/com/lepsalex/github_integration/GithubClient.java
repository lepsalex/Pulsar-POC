package com.lepsalex.github_integration;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class GithubClient {
    GitHub gitHub;

    final private Map<String, String> projectToRepoMapping = new HashMap<String, String>()
    {
        {
            put("awesome", "lepsalex/Pulsar-POC");
            put("possum", "lepsalex/Pulsar-POC");
            put("murder", "lepsalex/Pulsar-POC");
        }
    };

    final private Map<String, String> statusMapping = new HashMap<String, GHIssueState>() {
        {
            {
                put("open", GHIssueState.OPEN);
                put("closed", GHIssueState.CLOSED);
            }
        };
    }

    public GithubClient() throws IOException {
        log.info("Connecting to Github ...");
        gitHub = GitHub.connect();
    }

    public void createIssue(Task task) throws IOException {
        val repo = getRepo(task.getProject());
        createIssue(task, repo);
    }

    public void createIssue(Task task, GHRepository repo) {
        try {
            repo.createIssue(task.getName())
                    .body(task.getDescription())
                    .label(task.getProject())
                    .create();
        } catch (IOException err) {
            log.error("Error getting github project!", err);
        }
    }

    public void updateIssue(Task task) {
        try {
            val repo = getRepo(task.getProject());
            val issues = repo.getIssues(GHIssueState.ALL);

        } catch (IOException err) {
            log.error("Error getting github project!", err);
        }
    }

    private static GHIssue findIssue(String title, String project) {

    }

    private GHRepository getRepo(String project) throws IOException {
        return gitHub.getRepository(projectToRepoMapping.get(project));
    }
}
