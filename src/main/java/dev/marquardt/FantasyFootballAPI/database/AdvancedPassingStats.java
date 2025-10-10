package dev.marquardt.FantasyFootballAPI.database;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "advancedpassingstats")
public class AdvancedPassingStats {
    @Id
    private int playerId;
    private String playerName;
    private int age;
    private String team;
    private String position;
    private int games;
    private int gamesStarted;
    private int rank;
    private int passAttempts;
    private int passComps;
    private int yardsAfterCatch;
    private int passDrops;
    private double passDropPercent;
    private int spikes;
    private int passPlayAction;
    private int playActionPassYards;
    private int passThrowaways;
    private int battedPasses;
    private int intendedAirYards;
    private int passesOnTarget;
    private double passAirYardsPerCompletion;
    private double passTargetYardsPerAttempt;
    private int poorPasses;
    private int passRPO;
    private int passRPOPassAttempts;
    private int yardsRPO;
    private int passYardsRPO;
    private int rushYardsRPO;
    private int rushAttemptsRPO;
    private int passesHurried;
    private int passesHit;
    private int completedAirYards;
    private int numScrambles;
    private double pocketTime;
    private double poorPassThrowRate;
    private int timesPressured;
    private int timesBlitzed;
    private double pressuredPercent;
    private double passYardsAfterCatchPerCompletion;
    private double scrambleYardsPerAttempt;
    private double passAirYardsPerAttempt;
    private double onTargetPassPercent;
    private String awards;
}
