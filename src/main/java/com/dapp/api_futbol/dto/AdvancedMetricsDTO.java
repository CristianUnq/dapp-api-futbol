package com.dapp.api_futbol.dto;

import java.util.List;

public class AdvancedMetricsDTO {
    public static class Stat {
        private long count;
        private double mean;
        private double median;
        private double stddev;
        private double min;
        private double max;

        public Stat() {}

        public Stat(long count, double mean, double median, double stddev, double min, double max) {
            this.count = count;
            this.mean = mean;
            this.median = median;
            this.stddev = stddev;
            this.min = min;
            this.max = max;
        }

        public long getCount() { return count; }
        public double getMean() { return mean; }
        public double getMedian() { return median; }
        public double getStddev() { return stddev; }
        public double getMin() { return min; }
        public double getMax() { return max; }
    }

    private Stat puntos;
    private Stat golesAFavor;
    private Stat golesEnContra;
    private Stat diferenciaDeGoles;
    private List<TeamMetricDTO> teams;

    public Stat getPuntos() { return puntos; }
    public void setPuntos(Stat puntos) { this.puntos = puntos; }

    public Stat getGolesAFavor() { return golesAFavor; }
    public void setGolesAFavor(Stat golesAFavor) { this.golesAFavor = golesAFavor; }

    public Stat getGolesEnContra() { return golesEnContra; }
    public void setGolesEnContra(Stat golesEnContra) { this.golesEnContra = golesEnContra; }

    public Stat getDiferenciaDeGoles() { return diferenciaDeGoles; }
    public void setDiferenciaDeGoles(Stat diferenciaDeGoles) { this.diferenciaDeGoles = diferenciaDeGoles; }

    public List<TeamMetricDTO> getTeams() { return teams; }
    public void setTeams(List<TeamMetricDTO> teams) { this.teams = teams; }
}
