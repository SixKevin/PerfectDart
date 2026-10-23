package com.perfectdart.controller;

import com.perfectdart.entity.Player;
import com.perfectdart.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping
    public List<Player> getAll() {
        return playerService.getAllPlayers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Player> getById(@PathVariable Long id) {
        return playerService.getPlayerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public List<Player> getByPseudo(@RequestParam String pseudo) {
        return playerService.getPlayersByPseudo(pseudo);
    }

    @PostMapping
    public ResponseEntity<Player> create(@RequestBody Player player) {
        return ResponseEntity.status(HttpStatus.CREATED).body(playerService.createPlayer(player));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Player> update(@PathVariable Long id, @RequestBody Player player) {
        return playerService.updatePlayer(id, player)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return playerService.deletePlayer(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
