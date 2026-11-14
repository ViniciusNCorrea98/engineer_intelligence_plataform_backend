package io.eip.security;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class HumanIdentityApiTest {
    @Autowired MockMvc mvc;
    @Autowired OrganizationMembershipRepository memberships;
    @Autowired SecurityAuditRepository audit;
    @Autowired HumanUserRepository users;
    @Autowired HumanSessionRepository sessions;

    @Test
    void registrationCreatesPersonalOrganizationAndSessionProtectsAdministrativeOperations() throws Exception {
        Identity identity = registerAndLogin();
        mvc.perform(get("/api/auth/me").cookie(cookie("EIP_SESSION", identity.session())))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(identity.userId()));
        mvc.perform(post("/api/organizations").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Blocked\"}"))
                .andExpect(status().isUnauthorized());
        MvcResult organization = mvc.perform(post("/api/organizations").cookie(cookie("EIP_SESSION", identity.session()))
                .header("X-CSRF-Token", identity.csrf()).header("X-Actor-Id", "forged")
                .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Authorized\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id", notNullValue())).andReturn();
        String organizationId = json(organization, "id");
        org.junit.jupiter.api.Assertions.assertTrue(memberships.findByUserIdAndOrganizationId(UUID.fromString(identity.userId()), UUID.fromString(organizationId)).isPresent());
        org.junit.jupiter.api.Assertions.assertTrue(audit.count() >= 3);
    }

    @Test
    void rejectsDuplicatePasswordFailuresCsrfCrossTenantAccessAndRevokedSessions() throws Exception {
        Identity first = registerAndLogin();
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + first.email() + "\",\"password\":\"correct-horse-battery\"}"))
                .andExpect(status().isConflict());
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + first.email() + "\",\"password\":\"wrong-password-value\"}"))
                .andExpect(status().isUnauthorized());
        mvc.perform(post("/api/organizations").cookie(cookie("EIP_SESSION", first.session()))
                .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"No CSRF\"}"))
                .andExpect(status().isUnauthorized());
        Identity second = registerAndLogin();
        MvcResult organization = mvc.perform(post("/api/organizations").cookie(cookie("EIP_SESSION", first.session()))
                .header("X-CSRF-Token", first.csrf()).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"First only\"}"))
                .andExpect(status().isCreated()).andReturn();
        String organizationId = json(organization, "id");
        mvc.perform(post("/api/organizations/{id}/projects", organizationId).cookie(cookie("EIP_SESSION", second.session()))
                .header("X-CSRF-Token", second.csrf()).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Denied\"}"))
                .andExpect(status().isForbidden());
        mvc.perform(post("/api/auth/logout").cookie(cookie("EIP_SESSION", first.session())).header("X-CSRF-Token", first.csrf()))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/auth/me").cookie(cookie("EIP_SESSION", first.session()))).andExpect(status().isUnauthorized());
    }

    @Test
    void protectsServiceIdentityAdministrationWhileLeavingTechnicalAuthenticationIndependent() throws Exception {
        Identity owner = registerAndLogin();
        MvcResult organization = mvc.perform(post("/api/organizations").cookie(cookie("EIP_SESSION", owner.session()))
                .header("X-CSRF-Token", owner.csrf()).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Collection\"}"))
                .andExpect(status().isCreated()).andReturn();
        String organizationId = json(organization, "id");
        MvcResult project = mvc.perform(post("/api/organizations/{id}/projects", organizationId).cookie(cookie("EIP_SESSION", owner.session()))
                .header("X-CSRF-Token", owner.csrf()).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"API\"}"))
                .andExpect(status().isCreated()).andReturn();
        String projectId = json(project, "id");
        mvc.perform(post("/api/projects/{id}/sources", projectId).header("X-Organization-Id", organizationId)
                .contentType(MediaType.APPLICATION_JSON).content("{\"sourceKey\":\"git\"}"))
                .andExpect(status().isUnauthorized());
        mvc.perform(post("/api/projects/{id}/sources", projectId).header("X-Organization-Id", organizationId)
                .header("X-Actor-Id", "forged").cookie(cookie("EIP_SESSION", owner.session())).header("X-CSRF-Token", owner.csrf())
                .contentType(MediaType.APPLICATION_JSON).content("{\"sourceKey\":\"git\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.sourceKey").value("git"));
    }

    @Test
    void rejectsExpiredPersistedSessions() throws Exception {
        Identity identity = registerAndLogin();
        HumanUser user = users.findById(UUID.fromString(identity.userId())).orElseThrow();
        String secret = "expired-session-secret";
        var encoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        HumanSession expired = sessions.save(new HumanSession(user, encoder.encode(secret), encoder.encode("csrf"), Instant.now().minusSeconds(1)));
        mvc.perform(get("/api/auth/me").cookie(cookie("EIP_SESSION", "eip_hs_" + expired.getId() + "." + secret)))
                .andExpect(status().isUnauthorized());
    }

    private Identity registerAndLogin() throws Exception {
        String email = "human-" + UUID.randomUUID() + "@example.test";
        MvcResult registration = mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"correct-horse-battery\"}"))
                .andExpect(status().isCreated()).andReturn();
        MvcResult login = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"correct-horse-battery\"}"))
                .andExpect(status().isOk()).andReturn();
        return new Identity(json(registration, "id"), email, cookieValue(login, "EIP_SESSION"), cookieValue(login, "EIP_CSRF"));
    }
    private String json(MvcResult result, String field) throws Exception { return new com.fasterxml.jackson.databind.ObjectMapper().readTree(result.getResponse().getContentAsString()).get(field).asText(); }
    private String cookieValue(MvcResult result, String name) { return result.getResponse().getHeaders("Set-Cookie").stream().filter(value -> value.startsWith(name + "=")).map(value -> value.substring(name.length() + 1, value.indexOf(';'))).findFirst().orElseThrow(); }
    private jakarta.servlet.http.Cookie cookie(String name, String value) { return new jakarta.servlet.http.Cookie(name, value); }
    private record Identity(String userId, String email, String session, String csrf) { }
}
