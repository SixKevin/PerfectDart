package com.perfectdart.controller;

import com.perfectdart.entity.Game;
import com.perfectdart.service.GameService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class GameController {

    private final GameService gameService;

    @GetMapping
    public List<Game> getAll() {
        return gameService.getAllGames();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Game> getById(@PathVariable Long id) {
        return gameService.getGameById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Game> create(@RequestBody Game game) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gameService.createGame(game));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Game> update(@PathVariable Long id, @RequestBody Game game) {
        return gameService.updateGame(id, game)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/players")
    public ResponseEntity<Game> updatePlayers(@PathVariable Long id, @RequestBody List<Long> playerIds) {
        return gameService.updateGamePlayers(id, playerIds)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return gameService.deleteGame(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}