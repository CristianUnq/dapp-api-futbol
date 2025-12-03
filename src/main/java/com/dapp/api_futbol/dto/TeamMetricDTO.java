package com.dapp.api_futbol.dto;

public class TeamMetricDTO {
    private Long id;
    private String name;
    private Integer puntos;
    private Integer golesAFavor;
    private Integer golesEnContra;
    private Integer diferenciaDeGoles;
    private Double zScorePuntos;
    // New categorical ratings (S..F)
    private String finishingOpportunities; // Finishing opportunities
    private String longRangeShotOpportunities; // Long-range shooting opportunities
    private String comebackAbility; // Ability to come back from behind
    private String chanceCreation; // Chance creation
    private String protectLead; // Protecting lead
    private String controlOpponentsHalf; // Control of the opponent's half
    private String aerialDuels; // Aerial duels

    public TeamMetricDTO() {}

    public TeamMetricDTO(Long id, String name, Integer puntos, Integer golesAFavor, Integer golesEnContra, Integer diferenciaDeGoles) {
        this.id = id;
        this.name = name;
        this.puntos = puntos;
        this.golesAFavor = golesAFavor;
        this.golesEnContra = golesEnContra;
        this.diferenciaDeGoles = diferenciaDeGoles;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getPuntos() { return puntos; }
    public void setPuntos(Integer puntos) { this.puntos = puntos; }

    public Integer getGolesAFavor() { return golesAFavor; }
    public void setGolesAFavor(Integer golesAFavor) { this.golesAFavor = golesAFavor; }

    public Integer getGolesEnContra() { return golesEnContra; }
    public void setGolesEnContra(Integer golesEnContra) { this.golesEnContra = golesEnContra; }

    public Integer getDiferenciaDeGoles() { return diferenciaDeGoles; }
    public void setDiferenciaDeGoles(Integer diferenciaDeGoles) { this.diferenciaDeGoles = diferenciaDeGoles; }

    public Double getzScorePuntos() { return zScorePuntos; }
    public void setzScorePuntos(Double zScorePuntos) { this.zScorePuntos = zScorePuntos; }

    public String getFinishingOpportunities() { return finishingOpportunities; }
    public void setFinishingOpportunities(String finishingOpportunities) { this.finishingOpportunities = finishingOpportunities; }

    public String getLongRangeShotOpportunities() { return longRangeShotOpportunities; }
    public void setLongRangeShotOpportunities(String longRangeShotOpportunities) { this.longRangeShotOpportunities = longRangeShotOpportunities; }

    public String getComebackAbility() { return comebackAbility; }
    public void setComebackAbility(String comebackAbility) { this.comebackAbility = comebackAbility; }

    public String getChanceCreation() { return chanceCreation; }
    public void setChanceCreation(String chanceCreation) { this.chanceCreation = chanceCreation; }

    public String getProtectLead() { return protectLead; }
    public void setProtectLead(String protectLead) { this.protectLead = protectLead; }

    public String getControlOpponentsHalf() { return controlOpponentsHalf; }
    public void setControlOpponentsHalf(String controlOpponentsHalf) { this.controlOpponentsHalf = controlOpponentsHalf; }

    public String getAerialDuels() { return aerialDuels; }
    public void setAerialDuels(String aerialDuels) { this.aerialDuels = aerialDuels; }
}
