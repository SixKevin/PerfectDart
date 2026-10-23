package com.perfectdart.service;

import com.perfectdart.entity.Game;
import com.perfectdart.repository.GameRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GameService {

    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    public Optional<Game> getGameById(Long id) {
        return gameRepository.findById(id);
    }

    public Game createGame(Game game) {
        return gameRepository.save(game);
    }

    public Optional<Game> updateGame(Long id, Game updated) {
        return gameRepository.findById(id).map(existing -> {
            existing.setMode(updated.getMode());
            existing.setStatut(updated.getStatut());
            return gameRepository.save(existing);
        });
    }

    public boolean deleteGame(Long id) {
        if (!gameRepository.existsById(id)) {
            return false;
        }
        gameRepository.deleteById(id);
        return true;
    }
}