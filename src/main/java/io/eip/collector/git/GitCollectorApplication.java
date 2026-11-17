package io.eip.collector.git;

import java.net.URI;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

/** One-shot command-line entry point configured exclusively through environment variables. */
public final class GitCollectorApplication {

    private GitCollectorApplication() {
    }

    public static void main(String[] args) {
        Map<String, String> environment = System.getenv();
        URI endpoint = ingestionEndpoint(environment.getOrDefault("EIP_API_URL", "http://localhost:8080"));
        UUID activityId = optionalUuid(environment.get("EIP_ACTIVITY_ID"));
        Double confidence = optionalDouble(environment.get("EIP_CORRELATION_CONFIDENCE"));
        GitCollector.Config config = new GitCollector.Config(
                endpoint,
                UUID.fromString(required(environment, "EIP_PROJECT_ID")),
                environment.getOrDefault("EIP_SOURCE_KEY", "git"),
                required(environment, "EIP_SERVICE_TOKEN"),
                Path.of(environment.getOrDefault("EIP_GIT_REPOSITORY_PATH", ".")),
                required(environment, "EIP_GIT_REPOSITORY_REFERENCE"),
                activityId,
                confidence);

        GitCollector.CollectionResult result = new GitCollector().collect(config);
        System.out.printf("Git evidence %s: evidenceId=%s commit=%s%n",
                result.duplicate() ? "already existed" : "created", result.evidenceId(), result.commitSha());
    }

    private static URI ingestionEndpoint(String apiUrl) {
        String base = apiUrl.endsWith("/") ? apiUrl : apiUrl + "/";
        return URI.create(base).resolve("api/ingestion/events");
    }

    private static String required(Map<String, String> environment, String name) {
        String value = environment.get(name);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " is required");
        }
        return value;
    }

    private static UUID optionalUuid(String value) {
        return value == null || value.isBlank() ? null : UUID.fromString(value);
    }

    private static Double optionalDouble(String value) {
        return value == null || value.isBlank() ? null : Double.valueOf(value);
    }
}
