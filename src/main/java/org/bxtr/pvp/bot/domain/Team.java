package org.bxtr.pvp.bot.domain;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "TEAM")
public class Team {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "PLAYER_ID", referencedColumnName = "ID")
    private Player player;

    @ManyToOne
    @JoinColumn(name = "TOURNAMENT_ID", referencedColumnName = "ID")
    private Tournament tournament;


    @Column(name = "IMAGE")
    private byte[] image;
}
