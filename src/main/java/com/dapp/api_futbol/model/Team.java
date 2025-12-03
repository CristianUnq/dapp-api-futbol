package com.dapp.api_futbol.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private Integer matchsPlayed;
    private Integer matchsWon;
    private Integer matchsDrew;
    private Integer matchsLost;
    private Integer goalsInFavor;
    private Integer goalsAgainst;
    private Integer goalsDifference;
    private Integer points;
    private String shotsPerMatch;
    private String possesion;
    private String passAccuracy;
    private String aerialDuels;
    private String rating;

    @OneToMany(mappedBy = "team")
    private List<Player> players;

    public void setName(String string) {
        name = string;
    }

    public String getId() {
        return id.toString();
    }

    public String getName() {
        return name;
    }

    public Integer getMatchsPlayed() {
        return matchsPlayed;
    }

    public void setMatchsPlayed(Integer matchsPlayed) {
        this.matchsPlayed = matchsPlayed;
    }

    public Integer getMatchsWon() {
        return matchsWon;
    }

    public void setMatchsWon(Integer matchsWon) {
        this.matchsWon = matchsWon;
    }

    public Integer getMatchsDrew() {
        return matchsDrew;
    }

    public void setMatchsDrew(Integer matchsDrew) {
        this.matchsDrew = matchsDrew;
    }

    public Integer getMatchsLost() {
        return matchsLost;
    }

    public void setMatchsLost(Integer matchsLost) {
        this.matchsLost = matchsLost;
    }

    public Integer getGoalsInFavor() {
        return goalsInFavor;
    }

    public void setGoalsInFavor(Integer goalsInFavor) {
        this.goalsInFavor = goalsInFavor;
    }

    public Integer getGoalsAgainst() {
        return goalsAgainst;
    }

    public void setGoalsAgainst(Integer goalsAgainst) {
        this.goalsAgainst = goalsAgainst;
    }

    public Integer getGoalsDifference() {
        return goalsDifference;
    }

    public void setGoalsDifference(Integer goalsDifference) {
        this.goalsDifference = goalsDifference;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public String getShotsPerMatch() {
        return shotsPerMatch;
    }

    public void setShotsPerMatch(String shotsPerMatch) {
        this.shotsPerMatch = shotsPerMatch;
    }

    public String getPossesion() {
        return possesion;
    }

    public void setPossesion(String possesion) {
        this.possesion = possesion;
    }

    public String getPassAccuracy() {
        return passAccuracy;
    }

    public void setPassAccuracy(String passAccuracy) {
        this.passAccuracy = passAccuracy;
    }

    public String getAerialDuels() {
        return aerialDuels;
    }

    public void setAerialDuels(String aerialDuels) {
        this.aerialDuels = aerialDuels;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}