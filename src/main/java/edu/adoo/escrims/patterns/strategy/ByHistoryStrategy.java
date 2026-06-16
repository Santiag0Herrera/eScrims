package edu.adoo.escrims.patterns.strategy;

import edu.adoo.escrims.domain.Postulacion;
import edu.adoo.escrims.domain.Scrim;

import java.util.Comparator;
import java.util.List;

public class ByHistoryStrategy implements MatchmakingStrategy {
    private final int partidasMinimas;

    public ByHistoryStrategy(int partidasMinimas) {
        this.partidasMinimas = partidasMinimas;
    }

    @Override
    public List<Postulacion> seleccionarJugadores(Scrim scrim) {
        return scrim.getPostulaciones().stream()
                .sorted(Comparator.comparing(Postulacion::getRolDeseado))
                .limit(scrim.getCantidadJugadores())
                .toList();
    }
}
