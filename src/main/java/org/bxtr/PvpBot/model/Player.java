package org.bxtr.PvpBot.model;

import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * Модель для игрока.
 */
@EqualsAndHashCode
@Entity
@Table(name = "PLAYER")
public class Player {

    /**
     * Идентификатор
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

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

    public Player() {
        //empty
    }

    public Long getId() {
        return id;
    }

    public Player setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Player setName(String name) {
        this.name = name;
        return this;
    }

    public String getFriendCode() {
        return friendCode;
    }

    public Player setFriendCode(String friendCode) {
        this.friendCode = friendCode;
        return this;
    }

    public String getInGameNickName() {
        return inGameNickName;
    }

    public Player setInGameNickName(String inGameNickName) {
        this.inGameNickName = inGameNickName;
        return this;
    }

    public String getTownName() {
        return townName;
    }

    public Player setTownName(String townName) {
        this.townName = townName;
        return this;
    }
}
