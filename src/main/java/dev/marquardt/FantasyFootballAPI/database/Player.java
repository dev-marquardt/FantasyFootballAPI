package dev.marquardt.FantasyFootballAPI.database;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// imports for getter and setter
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "NFLPlayerData")
public class Player {
    @Id
    private int playerId;
    private String fullName;
    private String firstName;
    private String lastName;
    private String searchFullName;
    private String searchFirstName;
    private String searchLastName;
    private int searchRank;
    private String sport;
    private String status;
    private String team;
    private String college;
    private String highSchool;
    private int age;
    private String birthDate;
    private String birthCity;
    private String birthState;
    private String birthCountry;
    private int yearsExp;
    private boolean isActive;
    private String depthChartPosition;
    private int depthChartOrder;
    private String position;
    private int number;
    private String height;
    private int weight;
    private long newsUpdated;
    private String injuryStatus;
    private String injuryNotes;
    private String injuryBodyPart;
    private String injuryStartDate;
    private String practicePart;
    private String practiceDesc;
    private int espnId;
    private String sportRadarId;
    private int yahooId;
}
