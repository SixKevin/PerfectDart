package com.perfectdart.controller;

import com.perfectdart.entity.Player;
import com.perfectdart.repository.GameRepository;
import com.perfectdart.repository.PlayerRepository;
import com.perfectdart.service.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class AuthControllerTest {

    private static final String PSEUDO = "nokiade";
    private static final String PASSWORD = "1234";
    private static final String LOGIN_BODY = """
            {"pseudo":"nokiade","password":"%s"}
            """;

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext applicationContext;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        gameRepository.deleteAll();
        playerRepository.deleteAll();
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .addFilters(springSecurityFilterChain)
                .build();
        Player player = new Player();
        player.setPseudo(PSEUDO);
        player.setEmail("nokiade@gmail.com");
        player.setLevel(1);
        player.setPassword(PASSWORD);
        playerService.createPlayer(player);
    }

    @Test
    void loginWithValidCredentialsReturnsJwtAndStoresHashedPassword() throws Exception {
        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LOGIN_BODY.formatted(PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode token = objectMapper.readTree(response).get("token");
        assertTrue(token.asText().split("\\.").length == 3);

        String storedPassword = playerRepository.findByPseudo(PSEUDO).orElseThrow().getPassword();
        assertNotEquals(PASSWORD, storedPassword);
        assertTrue(passwordEncoder.matches(PASSWORD, storedPassword));
    }

    @Test
    void loginWithWrongPasswordReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LOGIN_BODY.formatted("wrong")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginIsPublicAndProtectedRoutesWithoutTokenReturnUnauthorized() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LOGIN_BODY.formatted(PASSWORD)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/games"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/players"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedRouteWithInvalidTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/games").header(HttpHeaders.AUTHORIZATION, "Bearer not-a-jwt"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedRoutesWithValidTokenReturnOkWithoutExposingPassword() throws Exception {
        String token = loginAndGetToken();

        mockMvc.perform(get("/games").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/players").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pseudo").value(PSEUDO))
                .andExpect(jsonPath("$[0].password").doesNotExist());
    }

    @Test
    void successfulLoginUpgradesLegacyPlaintextPassword() throws Exception {
        Player player = playerRepository.findByPseudo(PSEUDO).orElseThrow();
        player.setPassword(PASSWORD);
        playerRepository.save(player);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LOGIN_BODY.formatted(PASSWORD)))
                .andExpect(status().isOk());

        String storedPassword = playerRepository.findByPseudo(PSEUDO).orElseThrow().getPassword();
        assertNotEquals(PASSWORD, storedPassword);
        assertTrue(passwordEncoder.matches(PASSWORD, storedPassword));
    }

    private String loginAndGetToken() throws Exception {
        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LOGIN_BODY.formatted(PASSWORD)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(response).get("token").asText();
    }
}
