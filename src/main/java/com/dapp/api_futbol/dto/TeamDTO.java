package com.dapp.api_futbol.dto;

public class TeamDTO {
    private String name;
    private String country;
    private String url;

    public TeamDTO(String name, String country, String url) {
        this.name = name;
        this.country = country;
        this.url = url;
    }

    // Getters y setters (o usar records de Java 16+ para simplificar)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "TeamDTO{" +
               "name='" + name + '\'' +
               ", country='" + country + '\'' +
               ", url='" + url + '\'' +
               '}';
    }
}