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
        int mmrMedio = (scrim.getMmrMinimo() + scrim.getMmrMaximo()) / 2;
        return scrim.getPostulaciones().stream()
                .filter(postulacion -> "ACEPTADA".equals(postulacion.getEstado()) || "PENDIENTE".equals(postulacion.getEstado()))
                .filter(postulacion -> tienePerfilParaJuego(postulacion, scrim))
                .sorted(Comparator.comparingInt(postulacion -> Math.abs(
                        postulacion.getUsuario().perfilPara(scrim.getJuego()).getMmr() - mmrMedio)))
                .limit(scrim.getCantidadJugadores())
                .toList();
    }

    private boolean tienePerfilParaJuego(Postulacion postulacion, Scrim scrim) {
        try {
            postulacion.getUsuario().perfilPara(scrim.getJuego());
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }
}
