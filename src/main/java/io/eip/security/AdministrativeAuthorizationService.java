package io.eip.security;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.eip.common.ForbiddenException;
import io.eip.common.NotFoundException;
import io.eip.project.Organization;
import io.eip.project.OrganizationRepository;
import io.eip.project.Project;
import io.eip.project.ProjectRepository;

/** Evaluates the small, scope-aware RBAC policy from ADR-011 at the backend boundary. */
@Service
public class AdministrativeAuthorizationService {
    private final HumanAuthenticationService authentication;
    private final OrganizationRepository organizations;
    private final ProjectRepository projects;
    private final OrganizationMembershipRepository organizationMemberships;
    private final ProjectMembershipRepository projectMemberships;
    private final AuditService audit;

    public AdministrativeAuthorizationService(HumanAuthenticationService authentication, OrganizationRepository organizations,
            ProjectRepository projects, OrganizationMembershipRepository organizationMemberships,
            ProjectMembershipRepository projectMemberships, AuditService audit) {
        this.authentication = authentication; this.organizations = organizations; this.projects = projects;
        this.organizationMemberships = organizationMemberships; this.projectMemberships = projectMemberships; this.audit = audit;
    }

    @Transactional(readOnly = true)
    public HumanAuthenticationService.AuthenticatedHuman authenticated(String token, String csrf, boolean write) {
        return write ? authentication.authenticateForWrite(token, csrf) : authentication.authenticate(token);
    }

    @Transactional(readOnly = true)
    public HumanAuthenticationService.AuthenticatedHuman requireOrganizationAdmin(String token, String csrf, UUID organizationId) {
        var human = authenticated(token, csrf, true);
        OrganizationMembership membership = organizationMemberships.findByUserIdAndOrganizationId(human.user().getId(), organizationId).orElse(null);
        if (membership == null || membership.getState() != MembershipState.ACTIVE || membership.getRole() != HumanRole.ORG_ADMIN) deny(human, "ORGANIZATION", organizationId.toString(), null);
        return human;
    }

    @Transactional(readOnly = true)
    public HumanAuthenticationService.AuthenticatedHuman requireProjectAdmin(String token, String csrf, UUID declaredOrganizationId, UUID projectId) {
        var human = authenticated(token, csrf, true);
        Project project = projects.findById(projectId).orElseThrow(() -> new NotFoundException("Project not found"));
        if (!project.getOrganization().getId().equals(declaredOrganizationId)) throw new NotFoundException("Project not found in organization");
        OrganizationMembership organization = organizationMemberships.findByUserIdAndOrganizationId(human.user().getId(), declaredOrganizationId).orElse(null);
        boolean organizationAdmin = organization != null && organization.getState() == MembershipState.ACTIVE && organization.getRole() == HumanRole.ORG_ADMIN;
        ProjectMembership projectMembership = projectMemberships.findByUserIdAndProjectId(human.user().getId(), projectId).orElse(null);
        boolean projectAdmin = projectMembership != null && projectMembership.getState() == MembershipState.ACTIVE && projectMembership.getRole() == HumanRole.PROJECT_ADMIN;
        if (!organizationAdmin && !projectAdmin) deny(human, "PROJECT", projectId.toString(), projectId);
        return human;
    }

    @Transactional(readOnly = true)
    public Organization organization(UUID organizationId) { return organizations.findById(organizationId).orElseThrow(() -> new NotFoundException("Organization not found")); }
    @Transactional(readOnly = true)
    public Project project(UUID projectId) { return projects.findById(projectId).orElseThrow(() -> new NotFoundException("Project not found")); }
    private void deny(HumanAuthenticationService.AuthenticatedHuman human, String type, String targetId, UUID projectId) {
        audit.record("HUMAN", human.user().getId().toString(), "ADMIN_AUTHORIZATION", type, targetId, projectId, "DENIED", "Insufficient role or scope");
        throw new ForbiddenException();
    }
}
