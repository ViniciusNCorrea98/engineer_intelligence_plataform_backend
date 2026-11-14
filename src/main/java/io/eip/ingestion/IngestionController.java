package io.eip.ingestion;

import java.time.Instant;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/api/ingestion")
public class IngestionController {

    private final IngestionService ingestion;

    public IngestionController(IngestionService i) {
        ingestion = i;
    }

    @PostMapping("/events")
    ResponseEntity<Response> ingest(@RequestHeader("Authorization") String authorization, @RequestHeader("Idempotency-Key") String key, @Valid @RequestBody EventRequest request) {
        IngestionService.Result result = ingestion.ingest(authorization, key, request.toDomain());
        return ResponseEntity.status(result.duplicate() ? HttpStatus.OK : HttpStatus.CREATED).body(new Response(result.evidenceId(), result.duplicate()));
    }

    record EventRequest(@NotBlank
            @Pattern(regexp = "1\\.0") String schemaVersion, @NotNull UUID projectId, @NotBlank
            @Size(max = 100) String sourceKey, @NotBlank
            @Size(max = 255) String eventId, @NotBlank
            @Size(max = 255) String eventType, @NotNull Instant occurredAt, Instant observedAt, @NotNull JsonNode attributes, CorrelationRequest correlation, @Size(max = 1000) String repositoryReference) {

        IngestionService.Request toDomain() {
            return new IngestionService.Request(schemaVersion, projectId, sourceKey, eventId, eventType, occurredAt, observedAt, attributes, correlation == null ? null : new IngestionService.Correlation(correlation.activityId(), correlation.confidence()), repositoryReference);
        }
    }

    record CorrelationRequest(UUID activityId, @DecimalMin("0.0")
            @DecimalMax("1.0") Double confidence) {}

    record Response(UUID evidenceId, boolean duplicate) {}
}
