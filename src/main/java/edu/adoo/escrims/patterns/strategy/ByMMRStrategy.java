package edu.adoo.escrims.patterns.strategy;

import edu.adoo.escrims.domain.Postulacion;
import edu.adoo.escrims.domain.Scrim;

import java.util.Comparator;
import java.util.List;

public class ByMMRStrategy implements MatchmakingStrategy {
    private final int toleranciaMMR;

    public ByMMRStrategy(int toleranciaMMR) {
        this.toleranciaMMR = toleranciaMMR;
    }

    @Override
    public List<Postulacion> seleccionarJugadores(Scrim scrim) {
        return scrim.getPostulaciones().stream()
                .filter(postulacion -> "ACEPTADA".equals(postulacion.getEstado()) || "PENDIENTE".equals(postulacion.getEstado()))
                .sorted(Comparator.comparingInt(postulacion -> Math.abs(postulacion.getUsuario().perfilPara(scrim.getJuego()).getMmr()
                        - ((scrim.getMmrMinimo() + scrim.getMmrMaximo()) / 2))))
                .limit(scrim.getCantidadJugadores())
                .toList();
    }
}
