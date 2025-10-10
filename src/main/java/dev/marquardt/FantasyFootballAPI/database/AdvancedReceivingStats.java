package dev.marquardt.FantasyFootballAPI.database;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "advancedreceivingstats")
public class AdvancedReceivingStats {
    @Id
    private int playerId;
    private String playerName;
    private int age;
    private String team;
    private String position;
    private int games;
    private int gamesStarted;
    private int rank;
    private int targets;
    private int receptions;
    private int recYards;
    private int drops;
    private double dropRate;
    private int yardsAfterCatch;
    private double yardsAfterCatchPerReception;
    private int airYards;
    private double airYardsPerCatch;
    private int receivingBrokenTackles;
    private double receptionsPerBrokenTackle;
    private double averageDepthOfTarget;
    private int interceptionsWhenTargeted;
    private int receivingFirstDowns;
    private double receivingPasserRating;
    private String awards;
}
