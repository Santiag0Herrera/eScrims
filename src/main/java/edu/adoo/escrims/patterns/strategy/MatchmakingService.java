package edu.adoo.escrims.patterns.strategy;

import edu.adoo.escrims.domain.Equipo;
import edu.adoo.escrims.domain.Postulacion;
import edu.adoo.escrims.domain.Scrim;

import java.util.List;
import java.util.Objects;

public class MatchmakingService {
    private MatchmakingStrategy strategy;

    public MatchmakingService(MatchmakingStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy);
    }

    public void setStrategy(MatchmakingStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy);
    }

    public List<Postulacion> ejecutarMatchmaking(Scrim scrim) {
        List<Postulacion> seleccionados = strategy.seleccionarJugadores(scrim);
        List<Equipo> equipos = scrim.getEquipos();
        scrim.reiniciarEquipos();
        int index = 0;
        for (Postulacion postulacion : seleccionados) {
            postulacion.aceptar();
            equipos.get(index % equipos.size()).agregarJugador(postulacion.getUsuario(), postulacion.getRolDeseado());
            index++;
        }
        return seleccionados;
    }
}
