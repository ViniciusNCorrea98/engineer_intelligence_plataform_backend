package io.eip.project;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.eip.common.NotFoundException;

@Service
/**
 * Owns the organization-to-project tenancy boundary used by all feature
 * modules.
 */
public class ProjectService {

    private final OrganizationRepository organizations;
    private final ProjectRepository projects;

    public ProjectService(OrganizationRepository organizations, ProjectRepository projects) {
        this.organizations = organizations;
        this.projects = projects;
    }

    @Transactional
    public Organization createOrganization(String name) {
        return organizations.save(new Organization(UUID.randomUUID(), name));
    }

    @Transactional
    public Project createProject(UUID organizationId, String name) {
        Organization organization = organizations.findById(organizationId)
                .orElseThrow(() -> new NotFoundException("Organization not found"));
        return projects.save(new Project(UUID.randomUUID(), name, organization));
    }

    @Transactional(readOnly = true)
    public Project projectInOrganization(UUID organizationId, UUID projectId) {
        Project project = projects.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found"));
        // Foreign projects use absence semantics to avoid tenancy disclosure.
        if (!project.getOrganization().getId().equals(organizationId)) {
            throw new NotFoundException("Project not found in organization");
        }
        return project;
    }
}
