package org.bxtr.PvpBot.service;

import lombok.RequiredArgsConstructor;
import org.bxtr.PvpBot.model.FightResult;
import org.bxtr.PvpBot.repository.FightResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FightResultService {

    private final FightResultRepository fightResultRepository;

    public void saveResult(FightResult fightResult) {
        fightResultRepository.save(fightResult);
    }

    public List<FightResult> findAll() {
        final List<FightResult> results = new ArrayList<>();
        fightResultRepository.findAll()
                .forEach(item -> results.add(item));
        return results;
    }
}
