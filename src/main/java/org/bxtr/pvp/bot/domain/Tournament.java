package org.bxtr.pvp.bot.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name = "TOURNAMENT")
public class Tournament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "TOURNAMENT_NAME")
    private String tournamentName;

    @Column(name = "URL")
    private String url;

    @ManyToMany
    @JoinTable(name = "TOURNAMENT_PARTICIPANT",
            joinColumns = @JoinColumn(name = "TOURNAMENT_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "PLAYER_ID", referencedColumnName = "ID"))
    private List<Player> players;
}
