package br.com.fiap.aguiabranca.suggestion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.fiap.aguiabranca.AbstractMongoIT;
import br.com.fiap.aguiabranca.user.domain.UserRole;
import br.com.fiap.aguiabranca.user.infra.UserDocument;
import br.com.fiap.aguiabranca.user.infra.UserRepository;

@AutoConfigureMockMvc
class SuggestionAcceptanceIT extends AbstractMongoIT {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("A-SUG-01 gestor envia a operador, alvo gestor é 422, operador lê e não cria")
    void aSug01_managerSendsOperatorReadsOperatorForbidden() throws Exception {
        Session manager = login("gestor@innovatecorp.com", "gest123");
        Session operator = login("operador@innovatecorp.com", "oper123");
        Session leader = login("lideranca@innovatecorp.com", "lider123");
        String message = "Priorize ideias da transformação digital " + UUID.randomUUID();

        JsonNode created = createSuggestion(manager.token(), operator.userId(), message);
        assertThat(created.get("authorUserId").asText()).isEqualTo(manager.userId());
        assertThat(created.get("authorName").asText()).isEqualTo("Carlos Gestor");
        assertThat(created.get("targetUserId").asText()).isEqualTo(operator.userId());
        assertThat(created.get("targetEmail").asText()).isEqualTo("operador@innovatecorp.com");
        assertThat(created.get("targetName").asText()).isEqualTo("Ana Operadora");
        assertThat(created.get("message").asText()).isEqualTo(message);
        assertThat(created.get("createdAt").asText()).isNotBlank();
        String suggestionId = created.get("id").asText();

        mockMvc.perform(post("/api/v1/suggestions")
                        .header("Authorization", "Bearer " + manager.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(manager.userId(), "Destino não pode ser outro gestor.")))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("BUSINESS_RULE_VIOLATION"));

        mockMvc.perform(post("/api/v1/suggestions")
                        .header("Authorization", "Bearer " + manager.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(UUID.randomUUID().toString(), "Destino inexistente na base.")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));

        String inactiveId = inactiveOperator();
        mockMvc.perform(post("/api/v1/suggestions")
                        .header("Authorization", "Bearer " + manager.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(inactiveId, "Operador inativo não recebe.")))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("BUSINESS_RULE_VIOLATION"));

        mockMvc.perform(post("/api/v1/suggestions")
                        .header("Authorization", "Bearer " + operator.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(operator.userId(), "Operador não envia sugestão.")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));

        assertThat(ids(listAll(operator.token()))).contains(suggestionId);
        assertThat(ids(listAll(manager.token()))).contains(suggestionId);
        assertThat(ids(listAll(leader.token()))).doesNotContain(suggestionId);

        JsonNode fromLeader = createSuggestion(leader.token(), operator.userId(), "Liderança também orienta o operador " + UUID.randomUUID());
        assertThat(ids(listAll(operator.token()))).contains(fromLeader.get("id").asText());
        assertThat(ids(listAll(leader.token()))).contains(fromLeader.get("id").asText());
        assertThat(ids(listAll(manager.token()))).doesNotContain(fromLeader.get("id").asText());
    }

    @Test
    @DisplayName("Sugestão exige message entre 10 e 1000 e a listagem vem por createdAt DESC")
    void messageBoundsAndCreatedAtDescending() throws Exception {
        Session manager = login("gestor@innovatecorp.com", "gest123");
        Session operator = login("operador@innovatecorp.com", "oper123");

        mockMvc.perform(post("/api/v1/suggestions")
                        .header("Authorization", "Bearer " + manager.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(operator.userId(), "curta")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        mockMvc.perform(post("/api/v1/suggestions")
                        .header("Authorization", "Bearer " + manager.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(operator.userId(), "a".repeat(1001))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        createSuggestion(manager.token(), operator.userId(), "Primeira mensagem válida " + UUID.randomUUID());
        createSuggestion(manager.token(), operator.userId(), "Segunda mensagem válida " + UUID.randomUUID());

        Instant previous = Instant.MAX;
        for (JsonNode item : listAll(manager.token())) {
            Instant createdAt = Instant.parse(item.get("createdAt").asText());
            assertThat(createdAt).isBeforeOrEqualTo(previous);
            previous = createdAt;
        }
    }

    @Test
    @DisplayName("OpenAPI publica sugestões sem PUT nem DELETE")
    void openApi_exposesSuggestionRoutesWithoutMutation() throws Exception {
        MvcResult docs = mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode paths = objectMapper.readTree(docs.getResponse().getContentAsString()).get("paths");
        JsonNode collection = paths.get("/api/v1/suggestions");
        assertThat(collection.get("get")).isNotNull();
        assertThat(collection.get("post")).isNotNull();
        assertThat(collection.has("put")).isFalse();
        assertThat(collection.has("delete")).isFalse();
        assertThat(paths.has("/api/v1/suggestions/{id}")).isFalse();
    }

    private JsonNode createSuggestion(String token, String targetUserId, String message) throws Exception {
        MvcResult created = mockMvc.perform(post("/api/v1/suggestions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(targetUserId, message)))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(created.getResponse().getContentAsString());
    }

    private String body(String targetUserId, String message) throws Exception {
        return objectMapper.writeValueAsString(Map.of("targetUserId", targetUserId, "message", message));
    }

    private List<JsonNode> listAll(String token) throws Exception {
        List<JsonNode> items = new ArrayList<>();
        int page = 0;
        int totalPages = 1;
        while (page < totalPages) {
            MvcResult result = mockMvc.perform(get("/api/v1/suggestions")
                            .header("Authorization", "Bearer " + token)
                            .param("page", String.valueOf(page))
                            .param("size", "100"))
                    .andExpect(status().isOk())
                    .andReturn();
            JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
            body.get("content").forEach(items::add);
            totalPages = Math.max(body.get("totalPages").asInt(), 1);
            page++;
        }
        return items;
    }

    private static List<String> ids(List<JsonNode> items) {
        return items.stream().map(item -> item.get("id").asText()).toList();
    }

    private String inactiveOperator() {
        String email = "inativo-" + UUID.randomUUID() + "@innovatecorp.com";
        UserDocument user = new UserDocument();
        user.setId(UUID.randomUUID().toString());
        user.setName("Operador Inativo");
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode("oper123"));
        user.setRole(UserRole.OPERATOR);
        user.setActive(false);
        return userRepository.save(user).getId();
    }

    private Session login(String email, String password) throws Exception {
        Session cached = SESSIONS.get(email);
        if (cached != null) {
            return cached;
        }
        synchronized (SESSIONS) {
            cached = SESSIONS.get(email);
            if (cached != null) {
                return cached;
            }
            MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"email":"%s","password":"%s"}
                                    """.formatted(email, password)))
                    .andExpect(status().isOk())
                    .andReturn();
            JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
            Session created = new Session(
                    body.get("accessToken").asText(),
                    body.get("user").get("id").asText()
            );
            SESSIONS.put(email, created);
            return created;
        }
    }

    private record Session(String token, String userId) {
    }
}
