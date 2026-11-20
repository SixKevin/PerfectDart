package com.perfectdart.service;

import com.perfectdart.entity.Game;
import com.perfectdart.entity.Player;
import com.perfectdart.repository.GameRepository;
import com.perfectdart.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;

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

    public Optional<Game> updateGamePlayers(Long id, List<Long> playerIds) {
        if (playerIds == null || playerIds.stream().anyMatch(Objects::isNull)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Les identifiants des joueurs sont requis.");
        }

        return gameRepository.findById(id).map(existing -> {
            List<Long> uniqueIds = playerIds.stream().distinct().toList();
            List<Player> players = playerRepository.findAllById(uniqueIds);
            if (players.size() != uniqueIds.size()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Un ou plusieurs joueurs sont introuvables.");
            }

            Map<Long, Player> playersById = players.stream()
                    .collect(Collectors.toMap(Player::getId, Function.identity()));
            existing.setPlayers(new ArrayList<>(uniqueIds.stream().map(playersById::get).toList()));
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