package org.bxtr.PvpBot.service;

import lombok.RequiredArgsConstructor;
import org.bxtr.PvpBot.model.Team;
import org.bxtr.PvpBot.repository.TeamRepositoryJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TeamService {

    private final TeamRepositoryJPA teamRepositoryJPA;

    public void save(Team team) {
        teamRepositoryJPA.save(team);
    }

    public Team find(String playerName) {
        return teamRepositoryJPA.getByPlayer_Name(playerName);
    }
}
