package org.bxtr.pvp.bot.domain;

import lombok.Data;

import javax.persistence.*;

/**
 * Модель для игрока.
 */
@Entity
@Table(name = "PLAYER")
@Data
public class Player {

    /**
     * Идентификатор
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    /**
     * Никнейм в телеграмме
     */
    @Column(name = "PLAYER_NAME")
    private String name;

    /**
     * Код для добавления в друзья из PoGO
     */
    @Column(name = "FRIEND_CODE")
    private String friendCode;

    /**
     * Ник в игре
     */
    @Column(name = "IN_GAME_NICKNAME")
    private String inGameNickName;

    /**
     * Название города из которого игрок
     */
    @Column(name = "TOWN_NAME")
    private String townName;
}
