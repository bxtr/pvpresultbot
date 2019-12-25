package org.bxtr.pvp.bot.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@EqualsAndHashCode
@Entity
@Table(name = "FIGHT_RESULT")
@Data
public class FightResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ONE", referencedColumnName = "ID")
    private Player one;

    @ManyToOne
    @JoinColumn(name = "TWO", referencedColumnName = "ID")
    private Player two;

    @Column(name = "RESULT_ONE")
    private Integer resultOne;

    @Column(name = "RESULT_TWO")
    private Integer resultTwo;

    @Column(name = "REGISTERED")
    private Boolean registered;
}
