package dev.marquardt.FantasyFootballAPI.database;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "fantasyrankings")
public class FantasyRankings {
    @Id
    private int playerId;
    private String playerName;
    private int age;
    private String team;
    private String position;
    private int games;
    private int gamesStarted;
    private int rank;
    private int fantasyRankOverall;
    private int fantasyPositionRank;
    private double fantasyPointsPPR;
    private double fantasyPoints;
    private double fanduelPoints;
    private double draftKingsPoints;
    private int vbd;
    private int passAttempts;
    private int passCompletions;
    private int passYards;
    private int passTouchDowns;
    private int passInterceptions;
    private int twoPointConversionPasses;
    private int rushAttempts;
    private int rushYards;
    private int rushYardsPerAttempt;
    private int rushTouchDowns;
    private int fumbles;
    private int fumblesLost;
    private int targets;
    private int receptions;
    private int recYards;
    private int yardsPerReception;
    private int receivingTouchDowns;
    private int totalTouchDowns;
    private int twoPointMade;
}
