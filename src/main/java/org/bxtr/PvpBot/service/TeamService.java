package org.bxtr.PvpBot.service;

import org.bxtr.PvpBot.model.Team;
import org.bxtr.PvpBot.repository.TeamRepositoryJPA;
import org.springframework.stereotype.Service;

@Service
public class TeamService {

    private final TeamRepositoryJPA teamRepositoryJPA;

    public TeamService(TeamRepositoryJPA teamRepositoryJPA) {
        this.teamRepositoryJPA = teamRepositoryJPA;
    }

    public void save(Team team) {
        teamRepositoryJPA.save(team);
    }

    public Team find(String playerName) {
        return teamRepositoryJPA.getByPlayer_Name(playerName);
    }
}
