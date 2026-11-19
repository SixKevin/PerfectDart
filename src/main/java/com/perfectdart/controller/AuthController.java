package com.perfectdart.controller;

import com.perfectdart.dto.LoginRequest;
import com.perfectdart.dto.LoginResponse;
import com.perfectdart.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return authService.login(request.pseudo(), request.password())
                .map(token -> ResponseEntity.ok(new LoginResponse(token)))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}