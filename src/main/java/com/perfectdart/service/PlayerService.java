package com.perfectdart.service;

import com.perfectdart.entity.Player;
import com.perfectdart.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;


    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    public Optional<Player> getPlayerById(Long id) {
        return playerRepository.findById(id);
    }

    public Optional<Player> getPlayersByPseudo(String pseudo) {
        return playerRepository.findByPseudo(pseudo);
    }

    public Player createPlayer(Player player) {
        return playerRepository.save(player);
    }

    public Optional<Player> updatePlayer(Long id, Player updated) {
        return playerRepository.findById(id).map(existing -> {
            existing.setPseudo(updated.getPseudo());
            existing.setEmail(updated.getEmail());
            existing.setLevel(updated.getLevel());
            existing.setPassword(updated.getPassword()); // <-- Ajouté ici
            return playerRepository.save(existing);
        });
    }

    public boolean deletePlayer(Long id) {
        if (!playerRepository.existsById(id)) {
            return false;
        }
        playerRepository.deleteById(id);
        return true;
    }
}
