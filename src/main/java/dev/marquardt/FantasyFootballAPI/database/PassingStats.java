package dev.marquardt.FantasyFootballAPI.database;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

//imports for getter and setter
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "passingstats")
public class PassingStats {
    @Id
    private int playerID;
    private String playerName;
    private int age;
    private String team;
    private String position;
    private int games;
    private int gamesStarted;
    private int rank;
    private String record;
    private int attempts;
    private int completions;
    private double compRate;
    private int yards;
    private int touchDowns;
    private double touchDownRate;
    private int interceptions;
    private double interceptionRate;
    private int firstDowns;
    private double passingSuccessRate;
    private int longestPass;
    private double yardsPerAttempt;
    private double adjustedYardsPerAttempt;
    private double yardsPerCompletion;
    private double yardsPerGame;
    private double passerRate;
    private double qbr;
    private int sacks;
    private double sackRate;
    private int yardsLost;
    private double netYardsGainedPerAttempt;
    private double adjustedNetYardsPerAttempt;
    private int comebacksLed;
    private int gameWinningDrive;
    private String awards;
}
