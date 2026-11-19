package com.perfectdart.service;

import com.perfectdart.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PlayerRepository playerRepository;
    private final Map<String, String> tokens = new ConcurrentHashMap<>();

    public Optional<String> login(String pseudo, String password) {
        return playerRepository.findByPseudo(pseudo)
                .filter(player -> player.getPassword() != null && player.getPassword().equals(password))
                .map(player -> createToken(player.getPseudo()));
    }

    public boolean isValid(String token) {
        return tokens.containsKey(token);
    }

    private String createToken(String pseudo) {
        String token = UUID.randomUUID().toString();
        tokens.put(token, pseudo);
        return token;
    }
}