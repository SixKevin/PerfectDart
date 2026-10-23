package com.perfectdart.controller;

import com.perfectdart.dto.LoginRequest;
import com.perfectdart.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final PlayerRepository playerRepository;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        return playerRepository.findByPseudo(request.pseudo())
                .filter(p -> p.getPassword() != null && p.getPassword().equals(request.password()))
                .map(p -> ResponseEntity.ok("Connexion réussie !"))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Identifiants incorrects"));
    }
}