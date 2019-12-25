package org.bxtr.pvp.bot.service;

import lombok.AllArgsConstructor;
import org.bxtr.pvp.bot.repository.FightResultRepository;
import org.bxtr.pvp.bot.domain.FightResult;
import org.bxtr.pvp.bot.domain.Player;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class FightResultService {

    private final FightResultRepository fightResultRepository;

    public void saveResult(FightResult fightResult) {
        fightResultRepository.save(fightResult);
    }

    public void saveResult(Player playerOne, Player playerTwo, Integer scoreOne, Integer scoreTwo) {
        FightResult fightResult = new FightResult();
        fightResult.setOne(playerOne);
        fightResult.setTwo(playerTwo);
        fightResult.setResultOne(scoreOne);
        fightResult.setResultTwo(scoreTwo);
        fightResult.setRegistered(false);
        fightResultRepository.save(fightResult);
    }

    public Player getWinner(FightResult fightResult) {
        Integer resultOne = fightResult.getResultOne();
        Integer resultTwo = fightResult.getResultTwo();

        if (resultOne != null && resultTwo != null
                && resultOne > resultTwo) {
            return fightResult.getOne();
        }

        return fightResult.getTwo();
    }

    public List<FightResult> findAll() {
        return fightResultRepository.findAll();
    }
}
