package io.eip.security;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import io.eip.common.ConflictException;

/** Explicit administration endpoints keep role assignment inside the actor's authorized scope. */
@RestController
@RequestMapping("/api")
public class MembershipController {
    private final AdministrativeAuthorizationService authorization;
    private final HumanUserRepository users;
    private final OrganizationMembershipRepository organizationMemberships;
    private final ProjectMembershipRepository projectMemberships;
    private final AuditService audit;
    public MembershipController(AdministrativeAuthorizationService authorization, HumanUserRepository users, OrganizationMembershipRepository organizationMemberships, ProjectMembershipRepository projectMemberships, AuditService audit) {
        this.authorization = authorization; this.users = users; this.organizationMemberships = organizationMemberships; this.projectMemberships = projectMemberships; this.audit = audit;
    }
    @PostMapping("/organizations/{organizationId}/memberships") @ResponseStatus(HttpStatus.CREATED)
    Map<String, Object> organizationMembership(@PathVariable UUID organizationId, @CookieValue(name = "EIP_SESSION", required = false) String token, @RequestHeader(name = "X-CSRF-Token", required = false) String csrf, @Valid @RequestBody MembershipRequest request) {
        var actor = authorization.requireOrganizationAdmin(token, csrf, organizationId);
        if (request.role() != HumanRole.ORG_ADMIN) throw new IllegalArgumentException("Organization memberships require ORG_ADMIN");
        if (organizationMemberships.findByUserIdAndOrganizationId(request.userId(), organizationId).isPresent()) throw new ConflictException("Membership already exists");
        HumanUser user = users.findById(request.userId()).orElseThrow(() -> new io.eip.common.NotFoundException("User not found"));
        OrganizationMembership membership = organizationMemberships.save(new OrganizationMembership(user, authorization.organization(organizationId), request.role()));
        audit.record("HUMAN", actor.user().getId().toString(), "ORGANIZATION_MEMBERSHIP_CREATED", "ORGANIZATION_MEMBERSHIP", membership.getId().toString(), null, "SUCCESS", null);
        return Map.of("id", membership.getId(), "role", membership.getRole());
    }
    @PostMapping("/projects/{projectId}/memberships") @ResponseStatus(HttpStatus.CREATED)
    Map<String, Object> projectMembership(@PathVariable UUID projectId, @RequestHeader("X-Organization-Id") UUID organizationId, @CookieValue(name = "EIP_SESSION", required = false) String token, @RequestHeader(name = "X-CSRF-Token", required = false) String csrf, @Valid @RequestBody MembershipRequest request) {
        var actor = authorization.requireProjectAdmin(token, csrf, organizationId, projectId);
        if (request.role() == HumanRole.ORG_ADMIN) throw new IllegalArgumentException("ORG_ADMIN is organization scoped");
        if (projectMemberships.findByUserIdAndProjectId(request.userId(), projectId).isPresent()) throw new ConflictException("Membership already exists");
        HumanUser user = users.findById(request.userId()).orElseThrow(() -> new io.eip.common.NotFoundException("User not found"));
        ProjectMembership membership = projectMemberships.save(new ProjectMembership(user, authorization.project(projectId), request.role()));
        audit.record("HUMAN", actor.user().getId().toString(), "PROJECT_MEMBERSHIP_CREATED", "PROJECT_MEMBERSHIP", membership.getId().toString(), projectId, "SUCCESS", null);
        return Map.of("id", membership.getId(), "role", membership.getRole());
    }
    record MembershipRequest(@NotNull UUID userId, @NotNull HumanRole role) { }
}
