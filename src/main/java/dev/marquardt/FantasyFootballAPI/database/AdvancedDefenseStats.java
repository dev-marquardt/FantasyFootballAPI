package dev.marquardt.FantasyFootballAPI.database;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "advanceddefensestats")
public class AdvancedDefenseStats {
    @Id
    private int playerId;
    private String playerName;
    private int age;
    private String team;
    private String position;
    private int games;
    private int gamesStarted;
    private int rank;
    private int sacks;
    private int blitz;
    private int interceptions;
    private int pressures;
    private int quarterBackHurries;
    private int tacklesMissed;
    private double missedTackleRate;
    private int combinedTackles;
    private int quarterBackKnockDowns;
    private int battedPasses;
    private int completionsAllowed;
    private int defensiveTargets;
    private double completionRateAllowed;
    private double yardsAllowedPerTarget;
    private double yardsAllowedPerAttempt;
    private double yardsAllowedPerCompletion;
    private int airYardsAllowed;
    private int yardsAfterCatchAllowed;
    private int timesTargeted;
    private int touchDownsAllowed;
    private double passerRatingAllowed;
    private int yardsAllowedOnCompletions;
    private String awards;
}
