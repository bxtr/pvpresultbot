package org.bxtr.PvpBot.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "TEAM")
public class Team {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "PLAYER_ID", referencedColumnName = "ID")
    private Player player;

    @ManyToOne
    @JoinColumn(name = "TOURNAMENT_ID", referencedColumnName = "ID")
    private Tournament tournament;

    @Lob
    @Column(name = "IMAGE")
    private byte[] image;
}
