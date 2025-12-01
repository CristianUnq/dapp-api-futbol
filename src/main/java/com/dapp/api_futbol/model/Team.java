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

    private Integer partidosJugados;
    private Integer partidosGanados;
    private Integer partidosEmpatados;
    private Integer partidosPerdidos;
    private Integer golesAFavor;
    private Integer golesEnContra;
    private Integer diferenciaDeGoles;
    private Integer puntos;
    private String tirosPp;
    private String posesion;
    private String aciertoPase;
    private String aereos;
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

    public Integer getPartidosJugados() {
        return partidosJugados;
    }

    public void setPartidosJugados(Integer partidosJugados) {
        this.partidosJugados = partidosJugados;
    }

    public Integer getPartidosGanados() {
        return partidosGanados;
    }

    public void setPartidosGanados(Integer partidosGanados) {
        this.partidosGanados = partidosGanados;
    }

    public Integer getPartidosEmpatados() {
        return partidosEmpatados;
    }

    public void setPartidosEmpatados(Integer partidosEmpatados) {
        this.partidosEmpatados = partidosEmpatados;
    }

    public Integer getPartidosPerdidos() {
        return partidosPerdidos;
    }

    public void setPartidosPerdidos(Integer partidosPerdidos) {
        this.partidosPerdidos = partidosPerdidos;
    }

    public Integer getGolesAFavor() {
        return golesAFavor;
    }

    public void setGolesAFavor(Integer golesAFavor) {
        this.golesAFavor = golesAFavor;
    }

    public Integer getGolesEnContra() {
        return golesEnContra;
    }

    public void setGolesEnContra(Integer golesEnContra) {
        this.golesEnContra = golesEnContra;
    }

    public Integer getDiferenciaDeGoles() {
        return diferenciaDeGoles;
    }

    public void setDiferenciaDeGoles(Integer diferenciaDeGoles) {
        this.diferenciaDeGoles = diferenciaDeGoles;
    }

    public Integer getPuntos() {
        return puntos;
    }

    public void setPuntos(Integer puntos) {
        this.puntos = puntos;
    }

    public String getTirosPp() {
        return tirosPp;
    }

    public void setTirosPp(String tirosPp) {
        this.tirosPp = tirosPp;
    }

    public String getPosesion() {
        return posesion;
    }

    public void setPosesion(String posesion) {
        this.posesion = posesion;
    }

    public String getAciertoPase() {
        return aciertoPase;
    }

    public void setAciertoPase(String aciertoPase) {
        this.aciertoPase = aciertoPase;
    }

    public String getAereos() {
        return aereos;
    }

    public void setAereos(String aereos) {
        this.aereos = aereos;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}