package org.bxtr.pvp.bot.repository;

import org.bxtr.pvp.bot.domain.FightResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FightResultRepository extends JpaRepository<FightResult, Long> {
}
