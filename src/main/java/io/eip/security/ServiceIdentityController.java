package io.eip.security;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/projects/{projectId}")
public class ServiceIdentityController {

    private final ServiceIdentityService services;
    private final AdministrativeAuthorizationService authorization;

    public ServiceIdentityController(ServiceIdentityService s, AdministrativeAuthorizationService authorization) {
        services = s; this.authorization = authorization;
    }

    @PostMapping("/sources")
    @ResponseStatus(HttpStatus.CREATED)
    Map<String, Object> source(@PathVariable UUID projectId, @RequestHeader("X-Organization-Id") UUID org, @CookieValue(name = "EIP_SESSION", required = false) String token, @RequestHeader(name = "X-CSRF-Token", required = false) String csrf, @Valid @RequestBody SourceRequest r) {
        var human = authorization.requireProjectAdmin(token, csrf, org, projectId);
        ProjectSource s = services.createSource(org, projectId, r.sourceKey(), human.user().getId().toString());
        return Map.of("id", s.getId(), "sourceKey", s.getSourceKey());
    }

    @PostMapping("/service-identities")
    @ResponseStatus(HttpStatus.CREATED)
    Map<String, Object> identity(@PathVariable UUID projectId, @RequestHeader("X-Organization-Id") UUID org, @CookieValue(name = "EIP_SESSION", required = false) String token, @RequestHeader(name = "X-CSRF-Token", required = false) String csrf, @Valid @RequestBody NameRequest r) {
        var human = authorization.requireProjectAdmin(token, csrf, org, projectId);
        ServiceIdentity i = services.createIdentity(org, projectId, r.name(), human.user().getId().toString());
        return Map.of("id", i.getId(), "name", i.getName(), "state", i.getState());
    }

    @PostMapping("/service-identities/{identityId}/credentials")
    @ResponseStatus(HttpStatus.CREATED)
    CredentialResponse issue(@PathVariable UUID projectId, @PathVariable UUID identityId, @RequestHeader("X-Organization-Id") UUID org, @CookieValue(name = "EIP_SESSION", required = false) String token, @RequestHeader(name = "X-CSRF-Token", required = false) String csrf) {
        var human = authorization.requireProjectAdmin(token, csrf, org, projectId);
        return CredentialResponse.from(services.issue(org, projectId, identityId, human.user().getId().toString()));
    }

    @PostMapping("/service-identities/{identityId}/credentials/{credentialId}/rotate")
    CredentialResponse rotate(@PathVariable UUID projectId, @PathVariable UUID identityId, @PathVariable UUID credentialId, @RequestHeader("X-Organization-Id") UUID org, @CookieValue(name = "EIP_SESSION", required = false) String token, @RequestHeader(name = "X-CSRF-Token", required = false) String csrf) {
        var human = authorization.requireProjectAdmin(token, csrf, org, projectId);
        return CredentialResponse.from(services.rotate(org, projectId, identityId, credentialId, human.user().getId().toString()));
    }

    @PostMapping("/service-identities/{identityId}/credentials/{credentialId}/revoke")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void revoke(@PathVariable UUID projectId, @PathVariable UUID identityId, @PathVariable UUID credentialId, @RequestHeader("X-Organization-Id") UUID org, @CookieValue(name = "EIP_SESSION", required = false) String token, @RequestHeader(name = "X-CSRF-Token", required = false) String csrf) {
        var human = authorization.requireProjectAdmin(token, csrf, org, projectId);
        services.revoke(org, projectId, identityId, credentialId, human.user().getId().toString());
    }

    @PostMapping("/service-identities/{identityId}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void suspend(@PathVariable UUID projectId, @PathVariable UUID identityId, @RequestHeader("X-Organization-Id") UUID org, @CookieValue(name = "EIP_SESSION", required = false) String token, @RequestHeader(name = "X-CSRF-Token", required = false) String csrf) {
        var human = authorization.requireProjectAdmin(token, csrf, org, projectId);
        services.setSuspended(org, projectId, identityId, true, human.user().getId().toString());
    }

    @PostMapping("/service-identities/{identityId}/grants")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void grant(@PathVariable UUID projectId, @PathVariable UUID identityId, @RequestHeader("X-Organization-Id") UUID org, @CookieValue(name = "EIP_SESSION", required = false) String token, @RequestHeader(name = "X-CSRF-Token", required = false) String csrf, @Valid @RequestBody GrantRequest r) {
        var human = authorization.requireProjectAdmin(token, csrf, org, projectId);
        services.grant(org, projectId, identityId, r.sourceKey(), r.scope(), human.user().getId().toString());
    }

    record SourceRequest(@NotBlank String sourceKey) {}

    record NameRequest(@NotBlank String name) {}

    record GrantRequest(@NotBlank String sourceKey, @NotNull ServiceScope scope) {}

    record CredentialResponse(UUID credentialId, String token) {

        static CredentialResponse from(ServiceIdentityService.IssuedCredential c) {
            return new CredentialResponse(c.credentialId(), c.token());
        }
    }
}
