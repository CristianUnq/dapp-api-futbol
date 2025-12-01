package com.dapp.api_futbol.dto;

public class TeamMetricDTO {
    private Long id;
    private String name;
    private Integer puntos;
    private Integer golesAFavor;
    private Integer golesEnContra;
    private Integer diferenciaDeGoles;
    private Double zScorePuntos;

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
}
