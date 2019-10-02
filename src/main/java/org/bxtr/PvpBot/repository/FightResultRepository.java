package org.bxtr.PvpBot.repository;

import org.bxtr.PvpBot.model.FightResult;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FightResultRepository extends CrudRepository<FightResult, Long> {
}
