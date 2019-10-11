package org.bxtr.PvpBot.repository;

import org.bxtr.PvpBot.model.Player;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerCrudRepository extends CrudRepository<Player, Long> {
    Optional<Player> findByNameIgnoreCase(String name);

    List<Player> findByNameContaining(String name);
}
