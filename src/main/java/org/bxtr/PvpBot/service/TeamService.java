package org.bxtr.PvpBot.service;

import org.bxtr.PvpBot.model.Team;
import org.bxtr.PvpBot.repository.TeamRepositoryJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TeamService {

    @Autowired
    private TeamRepositoryJPA teamRepositoryJPA;

    public void save(Team team) {
        teamRepositoryJPA.save(team);
    }

    public Team find(String playerName) {
        return teamRepositoryJPA.getTeam(playerName);
    }
}
