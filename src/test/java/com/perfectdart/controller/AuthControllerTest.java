package com.perfectdart.controller;

import com.perfectdart.config.AuthInterceptor;
import com.perfectdart.entity.Player;
import com.perfectdart.repository.PlayerRepository;
import com.perfectdart.service.AuthService;
import com.perfectdart.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private static final String PSEUDO = "nokiade";
    private static final String PASSWORD = "1234";
    private static final String VALID_BODY = """
            {"pseudo": "nokiade", "password": "1234"}
            """;
    private static final String WRONG_PASSWORD_BODY = """
            {"pseudo": "nokiade", "password": "wrong"}
            """;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private GameService gameService;

    private AuthService authService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        authService = new AuthService(playerRepository);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new AuthController(authService), new GameController(gameService))
                .addMappedInterceptors(new String[]{"/games/**"}, new AuthInterceptor(authService))
                .build();
    }

    @Test
    void loginWithValidCredentialsReturnsToken() throws Exception {
        givenExistingPlayer();

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("token")));
    }

    @Test
    void loginWithWrongPasswordReturnsUnauthorized() throws Exception {
        givenExistingPlayer();

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(WRONG_PASSWORD_BODY))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedRouteWithoutTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/games"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedRouteWithInvalidTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/games").header(HttpHeaders.AUTHORIZATION, "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedRouteWithValidTokenReturnsOk() throws Exception {
        givenExistingPlayer();
        when(gameService.getAllGames()).thenReturn(List.of());
        String token = authService.login(PSEUDO, PASSWORD).orElseThrow();

        mockMvc.perform(get("/games").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
    }

    private void givenExistingPlayer() {
        Player player = new Player();
        player.setPseudo(PSEUDO);
        player.setEmail("nokiade@gmail.com");
        player.setLevel(1);
        player.setPassword(PASSWORD);
        when(playerRepository.findByPseudo(PSEUDO)).thenReturn(Optional.of(player));
    }

}