package org.bxtr.pvp.bot.service;

import lombok.AllArgsConstructor;
import org.bxtr.pvp.bot.domain.Team;
import org.bxtr.pvp.bot.repository.TeamRepositoryJPA;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TeamService {

    private final TeamRepositoryJPA teamRepositoryJPA;

    public void save(Team team) {
        teamRepositoryJPA.save(team);
    }

    public Team find(String playerName) {
        return teamRepositoryJPA.getByPlayerName(playerName)
                .orElse(null);
    }
}
