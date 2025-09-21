package com.dapp.api_futbol.dto;

public class PlayerDTO {

    private String name;
    private String team;
    private String url;
    private String height;
    private String weight;
    private String appearances;
    private String minsPlayed;
    private String goals;
    private String assists;
    private String yellowCards;
    private String redCards;
    private String shotsPerGame;
    private String passSuccess;
    private String aerialsWon;
    private String manOfTheMatch;
    private String rating;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getAppearances() {
        return appearances;
    }

    public void setAppearances(String appearances) {
        this.appearances = appearances;
    }

    public String getMinsPlayed() {
        return minsPlayed;
    }

    public void setMinsPlayed(String minsPlayed) {
        this.minsPlayed = minsPlayed;
    }

    public String getGoals() {
        return goals;
    }

    public void setGoals(String goals) {
        this.goals = goals;
    }

    public String getAssists() {
        return assists;
    }

    public void setAssists(String assists) {
        this.assists = assists;
    }

    public String getYellowCards() {
        return yellowCards;
    }

    public void setYellowCards(String yellowCards) {
        this.yellowCards = yellowCards;
    }

    public String getRedCards() {
        return redCards;
    }

    public void setRedCards(String redCards) {
        this.redCards = redCards;
    }

    public String getShotsPerGame() {
        return shotsPerGame;
    }

    public void setShotsPerGame(String shotsPerGame) {
        this.shotsPerGame = shotsPerGame;
    }

    public String getPassSuccess() {
        return passSuccess;
    }

    public void setPassSuccess(String passSuccess) {
        this.passSuccess = passSuccess;
    }

    public String getAerialsWon() {
        return aerialsWon;
    }

    public void setAerialsWon(String aerialsWon) {
        this.aerialsWon = aerialsWon;
    }

    public String getManOfTheMatch() {
        return manOfTheMatch;
    }

    public void setManOfTheMatch(String manOfTheMatch) {
        this.manOfTheMatch = manOfTheMatch;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "PlayerDTO{" +
                "name='" + name + '\'' +
                ", team='" + team + '\'' +
                ", url='" + url + '\'' +
                ", height='" + height + '\'' +
                ", weight='" + weight + '\'' +
                ", appearances='" + appearances + '\'' +
                ", minsPlayed='" + minsPlayed + '\'' +
                ", goals='" + goals + '\'' +
                ", assists='" + assists + '\'' +
                ", yellowCards='" + yellowCards + '\'' +
                ", redCards='" + redCards + '\'' +
                ", shotsPerGame='" + shotsPerGame + '\'' +
                ", passSuccess='" + passSuccess + '\'' +
                ", aerialsWon='" + aerialsWon + '\'' +
                ", manOfTheMatch='" + manOfTheMatch + '\'' +
                ", rating='" + rating + '\'' +
                '}';
    }
}