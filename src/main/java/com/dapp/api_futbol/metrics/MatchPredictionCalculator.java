package com.dapp.api_futbol.metrics;

import com.dapp.api_futbol.model.Team;
import com.dapp.api_futbol.dto.MatchPredictionDTO;
import org.springframework.stereotype.Component;

@Component
public class MatchPredictionCalculator {

    public MatchPredictionDTO calculatePrediction(
        Team localTeam,
        Team visitorTeam
    ) {
        // Inicializamos las probabilidades
        double localWinProbability = 0.33;
        double visitorWinProbability = 0.33;
        double drawProbability = 0.34;

        // --- Lógica de Comparación y Predicción Avanzada ---
        // Pesos para ajustar las probabilidades. Puedes tunear estos valores.
        final double RATING_WEIGHT = 0.15;
        final double POINTS_WEIGHT = 0.10;
        final double GOALS_DIFF_WEIGHT = 0.10;
        final double WINS_WEIGHT = 0.08;
        final double DRAWS_WEIGHT = 0.04; // Empates benefician a ambos para el empate
        final double LOSSES_WEIGHT = 0.08;
        final double GOALS_FOR_WEIGHT = 0.07;
        final double GOALS_AGAINST_WEIGHT = 0.07;
        final double SHOTS_PP_WEIGHT = 0.05;
        final double POSSESSION_WEIGHT = 0.05;
        final double PASS_ACCURACY_WEIGHT = 0.05;
        final double AERIALS_WEIGHT = 0.03;

        double localRating = Double.parseDouble(localTeam.getRating());
        double visitorRating = Double.parseDouble(visitorTeam.getRating());
        double localAereos = Double.parseDouble(localTeam.getAereos());
        double visitorAereos = Double.parseDouble(visitorTeam.getAereos());
        double localPosesion = Double.parseDouble(localTeam.getPosesion());
        double visitorPosesion = Double.parseDouble(visitorTeam.getPosesion());
        double localAciertoPase = Double.parseDouble(localTeam.getAciertoPase());
        double visitorAciertoPase = Double.parseDouble(visitorTeam.getAciertoPase());
        double localTirospp = Double.parseDouble(localTeam.getTirosPp());
        double visitorTirospp = Double.parseDouble(visitorTeam.getTirosPp());

        // 1. Comparar Rating
        if (localTeam.getRating() != null && visitorTeam.getRating() != null) {
            if (localRating > visitorRating) {
                localWinProbability += RATING_WEIGHT;
                visitorWinProbability -= RATING_WEIGHT / 2;
                drawProbability -= RATING_WEIGHT / 2;
            } else if (visitorRating > localRating) {
                visitorWinProbability += RATING_WEIGHT;
                localWinProbability -= RATING_WEIGHT / 2;
                drawProbability -= RATING_WEIGHT / 2;
            }
        }

        // 2. Comparar Puntos
        if (localTeam.getPuntos() != null && visitorTeam.getPuntos() != null) {
            if (localTeam.getPuntos() > visitorTeam.getPuntos()) {
                localWinProbability += POINTS_WEIGHT;
                visitorWinProbability -= POINTS_WEIGHT / 2;
                drawProbability -= POINTS_WEIGHT / 2;
            } else if (visitorTeam.getPuntos() > localTeam.getPuntos()) {
                visitorWinProbability += POINTS_WEIGHT;
                localWinProbability -= POINTS_WEIGHT / 2;
                drawProbability -= POINTS_WEIGHT / 2;
            }
        }

        // 3. Comparar Diferencia de Goles
        if (localTeam.getDiferenciaDeGoles() != null && visitorTeam.getDiferenciaDeGoles() != null) {
            if (localTeam.getDiferenciaDeGoles() > visitorTeam.getDiferenciaDeGoles()) {
                localWinProbability += GOALS_DIFF_WEIGHT;
                visitorWinProbability -= GOALS_DIFF_WEIGHT / 2;
                drawProbability -= GOALS_DIFF_WEIGHT / 2;
            } else if (visitorTeam.getDiferenciaDeGoles() > localTeam.getDiferenciaDeGoles()) {
                visitorWinProbability += GOALS_DIFF_WEIGHT;
                localWinProbability -= GOALS_DIFF_WEIGHT / 2;
                drawProbability -= GOALS_DIFF_WEIGHT / 2;
            }
        }

        // 4. Comparar Partidos Ganados
        if (localTeam.getPartidosGanados() != null && visitorTeam.getPartidosGanados() != null) {
            if (localTeam.getPartidosGanados() > visitorTeam.getPartidosGanados()) {
                localWinProbability += WINS_WEIGHT;
                visitorWinProbability -= WINS_WEIGHT / 2;
                drawProbability -= WINS_WEIGHT / 2;
            } else if (visitorTeam.getPartidosGanados() > localTeam.getPartidosGanados()) {
                visitorWinProbability += WINS_WEIGHT;
                localWinProbability -= WINS_WEIGHT / 2;
                drawProbability -= WINS_WEIGHT / 2;
            }
        }

        // 5. Comparar Partidos Empatados (mayor cantidad de empates puede indicar una tendencia al empate)
        if (localTeam.getPartidosEmpatados() != null && visitorTeam.getPartidosEmpatados() != null) {
            if (localTeam.getPartidosEmpatados() > visitorTeam.getPartidosEmpatados()) {
                drawProbability += DRAWS_WEIGHT;
                localWinProbability -= DRAWS_WEIGHT / 4; // Ajuste menor en victorias
                visitorWinProbability -= DRAWS_WEIGHT / 4; 
            } else if (visitorTeam.getPartidosEmpatados() > localTeam.getPartidosEmpatados()) {
                drawProbability += DRAWS_WEIGHT;
                localWinProbability -= DRAWS_WEIGHT / 4;
                visitorWinProbability -= DRAWS_WEIGHT / 4;
            }
        }

        // 6. Comparar Partidos Perdidos (menos perdidos = mejor equipo)
        if (localTeam.getPartidosPerdidos() != null && visitorTeam.getPartidosPerdidos() != null) {
            if (localTeam.getPartidosPerdidos() < visitorTeam.getPartidosPerdidos()) { // Menos pérdidas es positivo
                localWinProbability += LOSSES_WEIGHT;
                visitorWinProbability -= LOSSES_WEIGHT / 2;
                drawProbability -= LOSSES_WEIGHT / 2;
            } else if (visitorTeam.getPartidosPerdidos() < localTeam.getPartidosPerdidos()) {
                visitorWinProbability += LOSSES_WEIGHT;
                localWinProbability -= LOSSES_WEIGHT / 2;
                drawProbability -= LOSSES_WEIGHT / 2;
            }
        }

        // 7. Comparar Goles a Favor
        if (localTeam.getGolesAFavor() != null && visitorTeam.getGolesAFavor() != null) {
            if (localTeam.getGolesAFavor() > visitorTeam.getGolesAFavor()) {
                localWinProbability += GOALS_FOR_WEIGHT;
                visitorWinProbability -= GOALS_FOR_WEIGHT / 2;
                drawProbability -= GOALS_FOR_WEIGHT / 2;
            } else if (visitorTeam.getGolesAFavor() > localTeam.getGolesAFavor()) {
                visitorWinProbability += GOALS_FOR_WEIGHT;
                localWinProbability -= GOALS_FOR_WEIGHT / 2;
                drawProbability -= GOALS_FOR_WEIGHT / 2;
            }
        }

        // 8. Comparar Goles en Contra (menos goles en contra = mejor defensa)
        if (localTeam.getGolesEnContra() != null && visitorTeam.getGolesEnContra() != null) {
            if (localTeam.getGolesEnContra() < visitorTeam.getGolesEnContra()) { // Menos goles en contra es positivo
                localWinProbability += GOALS_AGAINST_WEIGHT;
                visitorWinProbability -= GOALS_AGAINST_WEIGHT / 2;
                drawProbability -= GOALS_AGAINST_WEIGHT / 2;
            } else if (visitorTeam.getGolesEnContra() < localTeam.getGolesEnContra()) {
                visitorWinProbability += GOALS_AGAINST_WEIGHT;
                localWinProbability -= GOALS_AGAINST_WEIGHT / 2;
                drawProbability -= GOALS_AGAINST_WEIGHT / 2;
            }
        }

        // 9. Comparar Tiros pp (Tiros por partido)
        if (localTeam.getTirosPp() != null && visitorTeam.getTirosPp() != null) {
            if (localTirospp > visitorTirospp) {
                localWinProbability += SHOTS_PP_WEIGHT;
                visitorWinProbability -= SHOTS_PP_WEIGHT / 2;
                drawProbability -= SHOTS_PP_WEIGHT / 2;
            } else if (visitorTirospp > localTirospp) {
                visitorWinProbability += SHOTS_PP_WEIGHT;
                localWinProbability -= SHOTS_PP_WEIGHT / 2;
                drawProbability -= SHOTS_PP_WEIGHT / 2;
            }
        }

        // 10. Comparar Posesión
        if (localTeam.getPosesion() != null && visitorTeam.getPosesion() != null) {
            if (localPosesion > visitorPosesion) {
                localWinProbability += POSSESSION_WEIGHT;
                visitorWinProbability -= POSSESSION_WEIGHT / 2;
                drawProbability -= POSSESSION_WEIGHT / 2;
            } else if (visitorPosesion > localPosesion) {
                visitorWinProbability += POSSESSION_WEIGHT;
                localWinProbability -= POSSESSION_WEIGHT / 2;
                drawProbability -= POSSESSION_WEIGHT / 2;
            }
        }

        // 11. Comparar Acierto de Pases
        if (localTeam.getAciertoPase() != null && visitorTeam.getAciertoPase() != null) {
            if (localAciertoPase > visitorAciertoPase) {
                localWinProbability += PASS_ACCURACY_WEIGHT;
                visitorWinProbability -= PASS_ACCURACY_WEIGHT / 2;
                drawProbability -= PASS_ACCURACY_WEIGHT / 2;
            } else if (visitorAciertoPase > localAciertoPase) {
                visitorWinProbability += PASS_ACCURACY_WEIGHT;
                localWinProbability -= PASS_ACCURACY_WEIGHT / 2;
                drawProbability -= PASS_ACCURACY_WEIGHT / 2;
            }
        }

        // 12. Comparar Aéreos Ganados
        if (localTeam.getAereos() != null && visitorTeam.getAereos() != null) {
            if (localAereos > visitorAereos) {
                localWinProbability += AERIALS_WEIGHT;
                visitorWinProbability -= AERIALS_WEIGHT / 2;
                drawProbability -= AERIALS_WEIGHT / 2;
            } else if (visitorAereos > localAereos) {
                visitorWinProbability += AERIALS_WEIGHT;
                localWinProbability -= AERIALS_WEIGHT / 2;
                drawProbability -= AERIALS_WEIGHT / 2;
            }
        }


        // Aseguramos que las probabilidades sumen 1 y estén entre 0 y 1
        // Normalización para que la suma sea 1.0
        double sum = localWinProbability + visitorWinProbability + drawProbability;
        if (sum <= 0) { // Evitar división por cero o resultados negativos/inválidos
            localWinProbability = 0.33;
            visitorWinProbability = 0.33;
            drawProbability = 0.34;
        } else {
            localWinProbability /= sum;
            visitorWinProbability /= sum;
            drawProbability /= sum;
        }

        // Limitar a un rango válido [0, 1]
        localWinProbability = Math.max(0, Math.min(1, localWinProbability));
        visitorWinProbability = Math.max(0, Math.min(1, visitorWinProbability));
        drawProbability = Math.max(0, Math.min(1, drawProbability));
        
        // Ajuste final para que la suma sea 1.0 (puede haber pequeñas desviaciones por el redondeo)
        sum = localWinProbability + visitorWinProbability + drawProbability;
        if (sum != 1.0) {
            double diff = 1.0 - sum;
            localWinProbability += diff / 3;
            visitorWinProbability += diff / 3;
            drawProbability += diff / 3;
        }

        MatchPredictionDTO prediction = new MatchPredictionDTO();
        prediction.setLocalTeamName(localTeam.getName());
        prediction.setVisitorTeamName(visitorTeam.getName());
        prediction.setLocalWinProbability(Math.round(localWinProbability * 100.0) / 100.0); // Redondeo a 2 decimales
        prediction.setVisitorWinProbability(Math.round(visitorWinProbability * 100.0) / 100.0);
        prediction.setDrawProbability(Math.round(drawProbability * 100.0) / 100.0);
        
        // Generar un resumen de la predicción
        String summary = "";
        if (localWinProbability > visitorWinProbability && localWinProbability > drawProbability) {
            summary = String.format("Es más probable que %s gane con %.2f%% de probabilidad.", localTeam.getName(), localWinProbability * 100);
        } else if (visitorWinProbability > localWinProbability && visitorWinProbability > drawProbability) {
            summary = String.format("Es más probable que %s gane con %.2f%% de probabilidad.", visitorTeam.getName(), visitorWinProbability * 100);
        } else if (drawProbability > localWinProbability && drawProbability > visitorWinProbability) {
            summary = String.format("Es más probable un empate con %.2f%% de probabilidad.", drawProbability * 100);
        } else {
            summary = "Las probabilidades están muy igualadas.";
        }
        prediction.setPredictionSummary(summary);

        return prediction;
    }
}
