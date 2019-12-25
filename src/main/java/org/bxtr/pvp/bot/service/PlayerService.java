package org.bxtr.pvp.bot.service;

import lombok.AllArgsConstructor;
import org.bxtr.pvp.bot.repository.PlayerCrudRepository;
import org.bxtr.pvp.bot.domain.Player;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static java.util.Optional.ofNullable;

@Service
@AllArgsConstructor
public class PlayerService {

    private final PlayerCrudRepository playerCrudRepository;

    public void createPlayer(String name) {
        ofNullable(name)
                .ifPresent(n -> {
                    Player player = new Player();
                    player.setName(name.toLowerCase());
                    playerCrudRepository.save(player);
                });
    }

    public Player findPlayer(String name) {
        return playerCrudRepository.findByNameIgnoreCase(name).orElse(null);
    }

    public List<Player> findAll() {
        return playerCrudRepository.findAll();
    }

    public List<Player> findLike(String name) {
        return ofNullable(name)
                .map(n -> playerCrudRepository.findByNameContaining(n.toLowerCase()))
                .orElse(Collections.emptyList());
    }

    public void updatePlayer(Player player) {
        ofNullable(player)
                .ifPresent(playerCrudRepository::save);
    }
}
