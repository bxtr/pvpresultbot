package org.bxtr.PvpBot.repository;

import org.bxtr.PvpBot.model.Player;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerCrudRepository extends CrudRepository<Player, Long> {
    Optional<Player> findByName(String name);
}
