package edu.adoo.escrims.patterns.strategy;

import edu.adoo.escrims.domain.Postulacion;
import edu.adoo.escrims.domain.Scrim;

import java.util.List;

public interface MatchmakingStrategy {
    List<Postulacion> seleccionarJugadores(Scrim scrim);
}
