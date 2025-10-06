package dev.marquardt.FantasyFootballAPI.database;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "advancedrushingstats")
public class AdvancedRushingStats {
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
    private int rushYardsAfterContact;
    private double rushYardsAfterContactPerRush;
    private int rushYardsBeforeContact;
    private double rushYardsBeforeContactPerRush;
    private int rushBrokenTackles;
    private double rushBrokenTacklesPerRush;
    private int rushFirstDowns;
    private String awards;
}
