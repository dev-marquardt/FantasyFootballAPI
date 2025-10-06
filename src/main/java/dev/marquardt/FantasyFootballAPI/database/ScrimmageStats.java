package dev.marquardt.FantasyFootballAPI.database;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// import fo getter and setter
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "scrimmagestats")
public class ScrimmageStats {
    @Id
    private int playerId;
    private String playerName;
    private int age;
    private String team;
    private String position;
    private int games;
    private int gamesStarted;
    private int rank;
    private int rushAttempts;
    private int rushYards;
    private double rushYardsPerAtt;
    private double rushYardsPerGame;
    private int longestRush;
    private double rushSuccessRate;
    private int rushTouchDowns;
    private int rushFirstDOwns;
    private double rushAttemptsPerGame;
    private int fumbles;
    private int targets;
    private int receptions;
    private double receptionsPerGame;
    private double catchRate;
    private double receptionSuccessRate;
    private int recYards;
    private double receivingYardsPerGame;
    private double receivingYardsPerReception;
    private int receivingFirstDowns;
    private int yardsFromScrimmage;
    private double receivingYardsPerTarget;
    private double yardsPerTouch;
    private int receivingTouchDowns;
    private int rushReceiveTouchDowns;
    private int longestReception;
    private int touches;
    private String awards;
}
