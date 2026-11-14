package io.eip.security;

import java.time.Instant;
import java.util.UUID;

import io.eip.project.Project;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "project_sources")
public class ProjectSource {

    @Id
    private UUID id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "project_id")
    private Project project;
    private String sourceKey;
    private Instant createdAt;

    protected ProjectSource() {
    }

    public ProjectSource(Project project, String sourceKey) {
        this.id = UUID.randomUUID();
        this.project = project;
        this.sourceKey = sourceKey;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public Project getProject() {
        return project;
    }

    public String getSourceKey() {
        return sourceKey;
    }
}
