package com.perfectdart.service;

import com.perfectdart.entity.Player;
import com.perfectdart.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Duration TOKEN_LIFETIME = Duration.ofHours(1);

    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    public Optional<String> login(String pseudo, String password) {
        return playerRepository.findByPseudo(pseudo)
                .filter(player -> verifyAndUpgradePassword(player, password))
                .map(player -> createToken(player.getPseudo()));
    }

    private boolean verifyAndUpgradePassword(Player player, String password) {
        String storedPassword = player.getPassword();
        if (storedPassword == null || password == null) {
            return false;
        }
        if (isBcryptHash(storedPassword) && passwordEncoder.matches(password, storedPassword)) {
            return true;
        }

        // Upgrade existing plaintext passwords after a successful login.
        if (!isBcryptHash(storedPassword) && storedPassword.equals(password)) {
            player.setPassword(passwordEncoder.encode(password));
            playerRepository.save(player);
            return true;
        }
        return false;
    }

    private boolean isBcryptHash(String value) {
        return value.startsWith("$2a$") || value.startsWith("$2b$") || value.startsWith("$2y$");
    }

    private String createToken(String pseudo) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("perfectdart")
                .issuedAt(now)
                .expiresAt(now.plus(TOKEN_LIFETIME))
                .subject(pseudo)
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}