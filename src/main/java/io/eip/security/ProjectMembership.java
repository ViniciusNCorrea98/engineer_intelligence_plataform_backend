package io.eip.security;

import java.time.Instant;
import java.util.UUID;

import io.eip.project.Project;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/** Project-scoped grant which prevents a project role from becoming an organization-wide privilege. */
@Entity
@Table(name = "project_memberships")
public class ProjectMembership {
    @Id private UUID id;
    @ManyToOne(optional = false) @JoinColumn(name = "user_id") private HumanUser user;
    @ManyToOne(optional = false) @JoinColumn(name = "project_id") private Project project;
    @Enumerated(EnumType.STRING) private HumanRole role;
    @Enumerated(EnumType.STRING) private MembershipState state;
    private Instant createdAt;
    private Instant updatedAt;
    protected ProjectMembership() { }
    public ProjectMembership(HumanUser user, Project project, HumanRole role) {
        id = UUID.randomUUID(); this.user = user; this.project = project; this.role = role;
        state = MembershipState.ACTIVE; createdAt = Instant.now(); updatedAt = createdAt;
    }
    public UUID getId() { return id; }
    public HumanRole getRole() { return role; }
    public MembershipState getState() { return state; }
    public void changeRole(HumanRole role) { this.role = role; updatedAt = Instant.now(); }
    public void suspend() { state = MembershipState.SUSPENDED; updatedAt = Instant.now(); }
}
