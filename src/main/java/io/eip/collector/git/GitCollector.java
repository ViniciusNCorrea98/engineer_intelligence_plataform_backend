package io.eip.collector.git;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/** Collects the current Git HEAD and sends one deterministic evidence event. */
public final class GitCollector {

    private static final String EVENT_TYPE = "git_commit_created";

    private final HttpClient http;
    private final ObjectMapper json;
    private final Clock clock;

    public GitCollector() {
        this(HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build(),
                new ObjectMapper().findAndRegisterModules(), Clock.systemUTC());
    }

    GitCollector(HttpClient http, ObjectMapper json, Clock clock) {
        this.http = Objects.requireNonNull(http);
        this.json = Objects.requireNonNull(json);
        this.clock = Objects.requireNonNull(clock);
    }

    public CollectionResult collect(Config config) {
        Objects.requireNonNull(config, "config is required");
        Path repository = validate(config);
        CommitSnapshot commit = snapshot(repository);
        String eventId = EVENT_TYPE + ":" + shortHash(config.repositoryReference()) + ":" + commit.sha();
        ObjectNode payload = payload(config, commit, eventId);

        HttpRequest request = HttpRequest.newBuilder(config.ingestionEndpoint())
                .timeout(Duration.ofSeconds(30))
                .header("Authorization", "Bearer " + config.serviceToken())
                .header("Idempotency-Key", eventId)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(write(payload), StandardCharsets.UTF_8))
                .build();

        try {
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() != 200 && response.statusCode() != 201) {
                throw new GitCollectorException("Ingestion rejected the event with HTTP " + response.statusCode());
            }
            JsonNode body = json.readTree(response.body());
            return new CollectionResult(UUID.fromString(body.path("evidenceId").asText()),
                    body.path("duplicate").asBoolean(), eventId, commit.sha());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new GitCollectorException("Interrupted while sending Git evidence", exception);
        } catch (IOException | IllegalArgumentException exception) {
            throw new GitCollectorException("Could not send Git evidence", exception);
        }
    }

    private Path validate(Config config) {
        if (config.ingestionEndpoint() == null || !List.of("http", "https").contains(config.ingestionEndpoint().getScheme())) {
            throw new IllegalArgumentException("An HTTP(S) ingestion endpoint is required");
        }
        if (config.projectId() == null) {
            throw new IllegalArgumentException("projectId is required");
        }
        requireText(config.sourceKey(), "sourceKey");
        if (config.sourceKey().length() > 100) {
            throw new IllegalArgumentException("sourceKey must have at most 100 characters");
        }
        requireText(config.serviceToken(), "serviceToken");
        requireText(config.repositoryReference(), "repositoryReference");
        if (config.repositoryReference().length() > 1000) {
            throw new IllegalArgumentException("repositoryReference must have at most 1000 characters");
        }
        if (config.activityId() == null && config.correlationConfidence() != null) {
            throw new IllegalArgumentException("correlationConfidence requires activityId");
        }
        if (config.correlationConfidence() != null && (config.correlationConfidence() < 0 || config.correlationConfidence() > 1)) {
            throw new IllegalArgumentException("correlationConfidence must be between 0 and 1");
        }
        try {
            return Objects.requireNonNull(config.repositoryPath(), "repositoryPath is required").toRealPath();
        } catch (IOException exception) {
            throw new IllegalArgumentException("repositoryPath does not exist", exception);
        }
    }

    private void requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " is required");
        }
    }

    private CommitSnapshot snapshot(Path repository) {
        String sha = git(repository, "rev-parse", "--verify", "HEAD");
        String treeSha = git(repository, "show", "-s", "--format=%T", "HEAD");
        String parents = git(repository, "show", "-s", "--format=%P", "HEAD");
        Instant committedAt;
        try {
            committedAt = Instant.parse(git(repository, "show", "-s", "--format=%cI", "HEAD"));
        } catch (RuntimeException exception) {
            throw new GitCollectorException("Git returned an invalid commit timestamp", exception);
        }
        String branch = gitOptional(repository, "symbolic-ref", "--quiet", "--short", "HEAD");
        List<String> parentShas = parents.isBlank() ? List.of() : List.of(parents.split("\\s+"));
        return new CommitSnapshot(sha, treeSha, parentShas, branch, committedAt);
    }

    private String git(Path repository, String... arguments) {
        GitCommandResult result = runGit(repository, arguments);
        if (result.exitCode() != 0) {
            throw new GitCollectorException("Git command failed: " + result.output());
        }
        return result.output();
    }

    private String gitOptional(Path repository, String... arguments) {
        GitCommandResult result = runGit(repository, arguments);
        return result.exitCode() == 0 ? result.output() : null;
    }

    private GitCommandResult runGit(Path repository, String... arguments) {
        List<String> command = new ArrayList<>();
        command.add("git");
        command.add("-C");
        command.add(repository.toString());
        command.addAll(List.of(arguments));
        ProcessBuilder processBuilder = new ProcessBuilder(command).redirectErrorStream(true);
        try {
            Process process = processBuilder.start();
            if (!process.waitFor(30, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                throw new GitCollectorException("Git command timed out");
            }
            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
            return new GitCommandResult(process.exitValue(), output);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new GitCollectorException("Interrupted while reading the Git repository", exception);
        } catch (IOException exception) {
            throw new GitCollectorException("Could not execute Git", exception);
        }
    }

    private ObjectNode payload(Config config, CommitSnapshot commit, String eventId) {
        ObjectNode attributes = json.createObjectNode();
        attributes.put("commit_sha", commit.sha());
        attributes.put("tree_sha", commit.treeSha());
        ArrayNode parents = attributes.putArray("parent_shas");
        commit.parentShas().forEach(parents::add);
        if (commit.branch() != null && !commit.branch().isBlank()) {
            attributes.put("branch", commit.branch());
        }

        ObjectNode payload = json.createObjectNode();
        payload.put("schemaVersion", "1.0");
        payload.put("projectId", config.projectId().toString());
        payload.put("sourceKey", config.sourceKey());
        payload.put("eventId", eventId);
        payload.put("eventType", EVENT_TYPE);
        payload.put("occurredAt", commit.committedAt().toString());
        payload.put("observedAt", clock.instant().toString());
        payload.set("attributes", attributes);
        payload.put("repositoryReference", config.repositoryReference());
        if (config.activityId() != null) {
            ObjectNode correlation = payload.putObject("correlation");
            correlation.put("activityId", config.activityId().toString());
            correlation.put("confidence", config.correlationConfidence() == null ? 1.0 : config.correlationConfidence());
        }
        return payload;
    }

    private String write(JsonNode payload) {
        try {
            return json.writeValueAsString(payload);
        } catch (IOException exception) {
            throw new GitCollectorException("Could not serialize Git evidence", exception);
        }
    }

    private String shortHash(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(digest, 0, 8);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }

    public record Config(URI ingestionEndpoint, UUID projectId, String sourceKey, String serviceToken,
            Path repositoryPath, String repositoryReference, UUID activityId, Double correlationConfidence) {
    }

    public record CollectionResult(UUID evidenceId, boolean duplicate, String eventId, String commitSha) {
    }

    private record CommitSnapshot(String sha, String treeSha, List<String> parentShas, String branch,
            Instant committedAt) {
    }

    private record GitCommandResult(int exitCode, String output) {
    }
}
