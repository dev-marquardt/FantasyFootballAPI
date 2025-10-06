package dev.marquardt.FantasyFootballAPI.database;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "redzonepassingstats")
public class RedZonePassingStats {
    @Id
    private int playerID;
    private String playerName;
    private String team;
    private int passAttempts;
    private int passCompletions;
    private double passCompletionRate;
    private int passYards;
    private int passTouchDowns;
    private int passInterceptions;
    private int passAttemptsInTen;
    private int passCompletionsInTen;
    private int passYardsInTen;
    private int passTouchDownsInTen;
    private int passInterceptionsInTen;
    private double passCompletionRateInTen;

}
