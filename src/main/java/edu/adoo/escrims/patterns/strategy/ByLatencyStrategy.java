package edu.adoo.escrims.patterns.strategy;

import edu.adoo.escrims.domain.Postulacion;
import edu.adoo.escrims.domain.Scrim;

import java.util.Comparator;
import java.util.List;

public class ByLatencyStrategy implements MatchmakingStrategy {
    private final int latenciaObjetivo;

    public ByLatencyStrategy(int latenciaObjetivo) {
        this.latenciaObjetivo = latenciaObjetivo;
    }

    @Override
    public List<Postulacion> seleccionarJugadores(Scrim scrim) {
        return scrim.getPostulaciones().stream()
                .sorted(Comparator.comparing(postulacion -> postulacion.getUsuario().getRegionPrincipal().getServidor()))
                .limit(scrim.getCantidadJugadores())
                .toList();
    }
}
