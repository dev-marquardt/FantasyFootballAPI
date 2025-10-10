package dev.marquardt.FantasyFootballAPI.database;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "redZonereceivingstats")
public class RedZoneReceivingStats {
    @Id
    private int playerId;
    private String playerName;
    private String team;
    private int targets;
    private int receptions;
    private int recYards;
    private int recTouchDowns;
    private double catchRate;
    private double percentOfTeamTargets;
    private int targetsInTen;
    private int receptionsInTen;
    private int receivingYardsInTen;
    private int receivingTouchDownsInTen;
    private double catchRateInTen;
    private double percentOfTeamTargetsInTen;
}
