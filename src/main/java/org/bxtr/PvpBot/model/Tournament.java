package org.bxtr.PvpBot.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "TOURNAMENT")
public class Tournament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "TOURNAMENT_NAME")
    private String tournamentName;

    @Column(name = "URL")
    private String url;
}
