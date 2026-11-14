package io.eip.activity;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class ActivityApiTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper objectMapper;
    private String session;
    private String csrf;

    @BeforeEach
    void authenticateAdministrator() throws Exception {
        String email = "activity-" + java.util.UUID.randomUUID() + "@example.test";
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"correct-horse-battery\"}"))
                .andExpect(status().isCreated());
        MvcResult login = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"correct-horse-battery\"}"))
                .andExpect(status().isOk()).andReturn();
        session = cookie(login, "EIP_SESSION");
        csrf = cookie(login, "EIP_CSRF");
    }

    @Test
    void createsCompletesAndReopensActivityWithAuditRequirement() throws Exception {
        String organizationId = createOrganization();
        String projectId = createProject(organizationId);
        String activityId = createActivity(organizationId, projectId);

        mvc.perform(post("/api/projects/{projectId}/activities/{activityId}/complete", projectId, activityId)
                        .header("X-Organization-Id", organizationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"successorTitle\":\"Follow-up work\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.successorActivityId", notNullValue()));

        mvc.perform(post("/api/projects/{projectId}/activities/{activityId}/reopen", projectId, activityId)
                        .header("X-Organization-Id", organizationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"actorId\":\"engineer-1\"}"))
                .andExpect(status().isBadRequest());

        mvc.perform(post("/api/projects/{projectId}/activities/{activityId}/reopen", projectId, activityId)
                        .header("X-Organization-Id", organizationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"actorId\":\"engineer-1\",\"justification\":\"Incorrect validation result\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    void declaredAssociationIsNotCanonicalUntilConfirmed() throws Exception {
        String organizationId = createOrganization();
        String projectId = createProject(organizationId);
        String activityId = createActivity(organizationId, projectId);

        MvcResult declaration = mvc.perform(post("/api/projects/{projectId}/activities/{activityId}/evidence-associations", projectId, activityId)
                        .header("X-Organization-Id", organizationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sourceSystem":"git","repositoryReference":"https://example.test/repo-b",
                                "evidenceReference":"commit:abc123","declaredBy":"collector:git","confidence":0.7}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.state").value("DECLARED"))
                .andExpect(jsonPath("$.canonicalRelationEstablishedAt").doesNotExist())
                .andReturn();
        String associationId = json(declaration, "id");

        mvc.perform(post("/api/projects/{projectId}/activities/{activityId}/evidence-associations/{associationId}/decision",
                        projectId, activityId, associationId)
                        .header("X-Organization-Id", organizationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"state\":\"CONFIRMED\",\"actorId\":\"engineer-1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("CONFIRMED"))
                .andExpect(jsonPath("$.canonicalRelationEstablishedAt", notNullValue()));
    }

    @Test
    void doesNotExposeProjectAcrossOrganizations() throws Exception {
        String organizationId = createOrganization();
        String differentOrganizationId = createOrganization();
        String projectId = createProject(organizationId);
        String activityId = createActivity(organizationId, projectId);

        mvc.perform(get("/api/projects/{projectId}/activities/{activityId}", projectId, activityId)
                        .header("X-Organization-Id", differentOrganizationId))
                .andExpect(status().isNotFound());
    }

    private String createOrganization() throws Exception {
        MvcResult result = mvc.perform(post("/api/organizations")
                        .cookie(new jakarta.servlet.http.Cookie("EIP_SESSION", session))
                        .header("X-CSRF-Token", csrf)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Acme\"}"))
                .andExpect(status().isCreated()).andReturn();
        return json(result, "id");
    }

    private String createProject(String organizationId) throws Exception {
        MvcResult result = mvc.perform(post("/api/organizations/{organizationId}/projects", organizationId)
                        .cookie(new jakarta.servlet.http.Cookie("EIP_SESSION", session))
                        .header("X-CSRF-Token", csrf)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Platform\"}"))
                .andExpect(status().isCreated()).andReturn();
        return json(result, "id");
    }

    private String createActivity(String organizationId, String projectId) throws Exception {
        MvcResult result = mvc.perform(post("/api/projects/{projectId}/activities", projectId)
                        .header("X-Organization-Id", organizationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Implement ingestion\",\"externalIssueReference\":\"ENG-42\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.externalIssueReference").value("ENG-42"))
                .andReturn();
        return json(result, "id");
    }

    private String json(MvcResult result, String field) throws Exception {
        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        return node.get(field).asText();
    }
    private String cookie(MvcResult result, String name) {
        return result.getResponse().getHeaders("Set-Cookie").stream()
                .filter(value -> value.startsWith(name + "="))
                .map(value -> value.substring(name.length() + 1, value.indexOf(';'))).findFirst().orElseThrow();
    }
}
