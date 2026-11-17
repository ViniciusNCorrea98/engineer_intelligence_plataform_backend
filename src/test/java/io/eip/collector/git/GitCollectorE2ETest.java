package io.eip.collector.git;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.eip.ingestion.IngestedEvidence;
import io.eip.ingestion.IngestedEvidenceRepository;
import io.eip.project.Organization;
import io.eip.project.Project;
import io.eip.project.ProjectService;
import io.eip.security.ServiceIdentity;
import io.eip.security.ServiceIdentityService;
import io.eip.security.ServiceScope;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GitCollectorE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private ProjectService projects;

    @Autowired
    private ServiceIdentityService identities;

    @Autowired
    private IngestedEvidenceRepository evidences;

    @Test
    void collectsRealGitHeadThroughAuthenticatedIngestionAndReplaysIdempotently(@TempDir Path repository)
            throws Exception {
        String commitSha = initializeRepository(repository);
        Fixture fixture = fixture();
        GitCollector collector = new GitCollector();
        GitCollector.Config config = new GitCollector.Config(
                URI.create("http://localhost:" + port + "/api/ingestion/events"),
                fixture.projectId(),
                "git",
                fixture.token(),
                repository,
                "https://example.test/acme/platform.git",
                null,
                null);

        GitCollector.CollectionResult created = collector.collect(config);
        GitCollector.CollectionResult replay = collector.collect(config);

        assertFalse(created.duplicate());
        assertTrue(replay.duplicate());
        assertEquals(created.evidenceId(), replay.evidenceId());
        assertEquals(1, evidences.countByProjectId(fixture.projectId()));

        IngestedEvidence evidence = evidences
                .findByProjectIdAndEventId(fixture.projectId(), created.eventId())
                .orElseThrow();
        assertTrue(created.eventId().startsWith("git_commit_created:"));
        assertTrue(created.eventId().endsWith(":" + commitSha));
        assertEquals("git_commit_created", evidence.getEventType());
        assertEquals("https://example.test/acme/platform.git", evidence.getRepositoryReference());
        assertEquals(commitSha, evidence.getAttributesJson().path("commit_sha").asText());
        assertEquals("main", evidence.getAttributesJson().path("branch").asText());
        assertTrue(evidence.getAttributesJson().path("parent_shas").isArray());
        assertTrue(evidence.getAttributesJson().path("parent_shas").isEmpty());
    }

    private Fixture fixture() {
        Organization organization = projects.createOrganization("git-collector-org-" + UUID.randomUUID());
        Project project = projects.createProject(organization.getId(), "git-collector-project");
        identities.createSource(organization.getId(), project.getId(), "git", "test-admin");
        ServiceIdentity identity = identities.createIdentity(
                organization.getId(), project.getId(), "git-collector", "test-admin");
        identities.grant(organization.getId(), project.getId(), identity.getId(), "git",
                ServiceScope.EVENTS_INGEST, "test-admin");
        ServiceIdentityService.IssuedCredential credential = identities.issue(
                organization.getId(), project.getId(), identity.getId(), "test-admin");
        return new Fixture(project.getId(), credential.token());
    }

    private String initializeRepository(Path repository) throws IOException, InterruptedException {
        git(repository, "init", "-b", "main");
        git(repository, "config", "user.name", "EIP Collector Test");
        git(repository, "config", "user.email", "collector-test@example.test");
        Files.writeString(repository.resolve("README.md"), "# collector fixture\n", StandardCharsets.UTF_8);
        git(repository, "add", "README.md");
        git(repository, "commit", "-m", "Create collector fixture");
        return git(repository, "rev-parse", "HEAD");
    }

    private String git(Path repository, String... arguments) throws IOException, InterruptedException {
        List<String> command = new java.util.ArrayList<>();
        command.add("git");
        command.addAll(List.of(arguments));
        Process process = new ProcessBuilder(command)
                .directory(repository.toFile())
                .redirectErrorStream(true)
                .start();
        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IllegalStateException("Git fixture command failed: " + output);
        }
        return output;
    }

    private record Fixture(UUID projectId, String token) {
    }
}
