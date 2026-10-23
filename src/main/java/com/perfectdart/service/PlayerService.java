package com.perfectdart.service;

import com.perfectdart.entity.Player;
import com.perfectdart.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    public Optional<Player> getPlayerById(Long id) {
        return playerRepository.findById(id);
    }

    public List<Player> getPlayersByPseudo(String pseudo) {
        return playerRepository.findByPseudo(pseudo);
    }

    public Player createPlayer(Player player) {
        return playerRepository.save(player);
    }

    public Optional<Player> updatePlayer(Long id, Player updated) {
        return playerRepository.findById(id).map(existing -> {
            existing.setPseudo(updated.getPseudo());
            existing.setEmail(updated.getEmail());
            existing.setNiveau(updated.getNiveau());
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
