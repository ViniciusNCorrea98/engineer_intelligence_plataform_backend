package io.eip.security;

import java.time.Instant;
import java.util.UUID;

import io.eip.project.Organization;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/** Organization-scoped grant; only an active ORG_ADMIN can administer the organization. */
@Entity
@Table(name = "organization_memberships")
public class OrganizationMembership {
    @Id private UUID id;
    @ManyToOne(optional = false) @JoinColumn(name = "user_id") private HumanUser user;
    @ManyToOne(optional = false) @JoinColumn(name = "organization_id") private Organization organization;
    @Enumerated(EnumType.STRING) private HumanRole role;
    @Enumerated(EnumType.STRING) private MembershipState state;
    private Instant createdAt;
    private Instant updatedAt;
    protected OrganizationMembership() { }
    public OrganizationMembership(HumanUser user, Organization organization, HumanRole role) {
        id = UUID.randomUUID(); this.user = user; this.organization = organization; this.role = role;
        state = MembershipState.ACTIVE; createdAt = Instant.now(); updatedAt = createdAt;
    }
    public UUID getId() { return id; }
    public HumanUser getUser() { return user; }
    public Organization getOrganization() { return organization; }
    public HumanRole getRole() { return role; }
    public MembershipState getState() { return state; }
    public void changeRole(HumanRole role) { this.role = role; updatedAt = Instant.now(); }
    public void suspend() { state = MembershipState.SUSPENDED; updatedAt = Instant.now(); }
}
