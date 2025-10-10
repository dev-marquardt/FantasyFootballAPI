package dev.marquardt.FantasyFootballAPI.database;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table (name = "redzonerushingstats")
public class RedZoneRushingStats {
    @Id
    private int playerId;
    private String playerName;
    private String team;
    private int rushAttempts;
    private int rushYards;
    private int rushTouchDowns;
    private double percentOfTeamRushes;
    private int rushAttemptsInTen;
    private int rushYardsInTen;
    private int rushTouchDownsInTen;
    private double percentOfTeamRushesInTen;
    private int rushAttemptsInFive;
    private int rushYardsInFive;
    private int rushTouchDownsInFive;
    private double percentOfTeamRushesInFive;
}
