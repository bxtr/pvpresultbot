package org.bxtr.pvp.bot.repository;

import org.bxtr.pvp.bot.domain.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerCrudRepository extends JpaRepository<Player, Long> {
    Optional<Player> findByNameIgnoreCase(String name);

    List<Player> findByNameContaining(String name);
}
