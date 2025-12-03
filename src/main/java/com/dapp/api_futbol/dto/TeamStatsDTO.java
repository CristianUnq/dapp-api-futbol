package com.dapp.api_futbol.dto;

public class TeamStatsDTO {
    private Long id;
    private String name;
    private String averageAgeDescription;
    private String averageRatingDescription;
    private String winRateDescription;
    private String bestPlayerName;

    public TeamStatsDTO() {

    }

    public TeamStatsDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAverageAgeDescription() { return averageAgeDescription; }
    public void setAverageAgeDescription(String averageAgeDescription) { this.averageAgeDescription = averageAgeDescription; }
    public String getAverageRatingDescription() { return averageRatingDescription; }
    public void setAverageRatingDescription(String averageRatingDescription) { this.averageRatingDescription = averageRatingDescription; }
    public String getWinRateDescription() { return winRateDescription; }
    public void setWinRateDescription(String winRateDescription) { this.winRateDescription = winRateDescription; }
    public String getBestPlayerName() { return bestPlayerName; }
    public void setBestPlayerName(String bestPlayerName) { this.bestPlayerName = bestPlayerName; }
}
