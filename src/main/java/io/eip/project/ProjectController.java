package io.eip.project;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import io.eip.security.AdministrativeAuthorizationService;
import io.eip.security.HumanRole;
import io.eip.security.OrganizationMembership;
import io.eip.security.OrganizationMembershipRepository;
import io.eip.security.AuditService;

@RestController
@RequestMapping("/api")
public class ProjectController {

    private final ProjectService projects;
    private final AdministrativeAuthorizationService authorization;
    private final OrganizationMembershipRepository memberships;
    private final AuditService audit;

    public ProjectController(ProjectService projects, AdministrativeAuthorizationService authorization, OrganizationMembershipRepository memberships, AuditService audit) {
        this.projects = projects;
        this.authorization = authorization;
        this.memberships = memberships;
        this.audit = audit;
    }

    @PostMapping("/organizations")
    @ResponseStatus(HttpStatus.CREATED)
    Map<String, Object> createOrganization(@org.springframework.web.bind.annotation.CookieValue(name = "EIP_SESSION", required = false) String token, @RequestHeader(name = "X-CSRF-Token", required = false) String csrf, @Valid @RequestBody NameRequest request) {
        var human = authorization.authenticated(token, csrf, true);
        Organization organization = projects.createOrganization(request.name());
        memberships.save(new OrganizationMembership(human.user(), organization, HumanRole.ORG_ADMIN));
        audit.record("HUMAN", human.user().getId().toString(), "ORGANIZATION_CREATED", "ORGANIZATION", organization.getId().toString(), null, "SUCCESS", null);
        return Map.of("id", organization.getId(), "name", organization.getName());
    }

    @PostMapping("/organizations/{organizationId}/projects")
    @ResponseStatus(HttpStatus.CREATED)
    Map<String, Object> createProject(@PathVariable UUID organizationId, @org.springframework.web.bind.annotation.CookieValue(name = "EIP_SESSION", required = false) String token, @RequestHeader(name = "X-CSRF-Token", required = false) String csrf, @Valid @RequestBody NameRequest request) {
        authorization.requireOrganizationAdmin(token, csrf, organizationId);
        Project project = projects.createProject(organizationId, request.name());
        var human = authorization.authenticated(token, csrf, true);
        audit.record("HUMAN", human.user().getId().toString(), "PROJECT_CREATED", "PROJECT", project.getId().toString(), project.getId(), "SUCCESS", null);
        return Map.of("id", project.getId(), "name", project.getName(), "organizationId", organizationId);
    }

    public record NameRequest(@NotBlank String name) {

    }
}
