package dev.marquardt.FantasyFootballAPI.database;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

// getter and setter


@Setter
@Getter
@Entity
@Table(name = "defensestats")
public class DefenseStats {
    @Id
    private int playerId;
    private String playerName;
    private int age;
    private String team;
    private String position;
    private int games;
    private int gamesStarted;
    private int rank;
    private int soloTackles;
    private int tackleAssists;
    private int comboTackles;
    private int tacklesForLoss;
    private double sacks;
    private int quarterBackHits;
    private int fumbles;
    private int fumblesForced;
    private int fumblesRecovered;
    private int fumbleRecYards;
    private int fumbleRecoveryTouchDowns;
    private int safety;
    private int interceptions;
    private int passesDefended;
    private int interceptionReturnYards;
    private int longestInterceptionReturn;
    private int interceptionReturnTouchDown;
    private String awards;
}
